//
//  WritingViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 25/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

@MainActor
class WritingViewModel: ObservableObject {
    private let topicRepo = TopicRepositoryHelper().topicRepo
    private let getAllDraftsTitleUseCase = UseCasesHelper().getAllDraftsTitleUseCase
    private let getLastOpenedUseCase = UseCasesHelper().getLastOpenedUseCase
    private let saveDraftUseCase = UseCasesHelper().saveDraftUseCase
    private let createNewDraftUseCase = UseCasesHelper().createNewDraftUseCase
    private let queryDraftUseCase = UseCasesHelper().queryDraftUseCase
    private let postStoryUseCase = UseCasesHelper().postStoryUseCase
    
    @Published var currentDraftId: String? = nil
    @Published var title: String = ""
    @Published var content: String = ""
    @Published var draftList: [String:String] = [String:String]()
    @Published var topicList: [String] = []
    @Published var selectedTopic: String = ""
    @Published var isPublished: Bool = false
    @Published var commentAllowed: Bool = false
    @Published var saveAllowed: Bool = false
    @Published var error: String? = nil
    @Published var postSuccess: Bool = false
    @Published var loading: Bool = false
    @Published var isUserSignedIn: Bool = false
    
    var menuItemList: [String] = ["Clear", "Edit History", "New Draft"]
    
    init() {
        refreshData()
    }
    
    func refreshData() {
        Task {
            do {
                let lastOpened = try await getLastOpenedUseCase.invoke()
                self.currentDraftId = lastOpened?.first as? String ?? ""
                self.title = lastOpened?.second?.title as? String ?? ""
                self.content = lastOpened?.second?.content as? String ?? ""
                self.getTopicList()
            } catch { print(error) }
        }
        
    }
    
    func onMenuClicked(indentifier: String) {
        switch indentifier {
        case "Clear":
            clearDraft()
        case "Edit History": break
        case "New Draft":
            createNewDraft()
        default: break
        }
    }
    
    func onMenuClicked(id: String) {
        switchDraft(id: id)
    }
    
    func setTitle(title: String) {
        self.title = title
    }
    
    func setContent(content: String){
        self.content = content
    }
    
    func clearDraft() {
        self.content = ""
    }
    
    func setPublished(isPublished: Bool) {
        self.isPublished = isPublished
        self.commentAllowed = self.commentAllowed && isPublished
        self.saveAllowed = self.saveAllowed && isPublished
    }
    
    func setCommentAllowed(commentAllowed: Bool) {
        self.commentAllowed = commentAllowed
    }
    
    func setSaveAllowed(saveAllowed: Bool) {
        self.saveAllowed = saveAllowed
    }
    
    func setTopic(topic: String) {
        self.selectedTopic = topic
    }
    
    func getAllDraftsTitle() {
        Task {
            do {
                try await getAllDraftsTitleUseCase.invoke().collect(collector: Collector<[String:String]> { response in
                    DispatchQueue.main.async {
                        self.draftList = response
                    }
                })
            } catch {
                print(error)
            }
        }
    }
    
    func getTopicList() {
        Task {
            let topicList = try await self.topicRepo().getAllTopic()
            self.topicList = topicList.map({ $0.name })
        }
    }
    
    func saveDraft() {
        Task {
            do {
                guard !self.title.isEmpty else { return }
                try await saveDraftUseCase.invoke(id: self.currentDraftId, title: self.title, content: self.content)
            } catch{ print(error) }
        }
    }
    
    func createNewDraft() {
        saveDraft()
        let result = createNewDraftUseCase.invoke()
        // Get data from result
        self.currentDraftId = result["id"] as? String
        self.title = result["title"] as! String
        self.selectedTopic = result["selectedTopic"] as! String
        self.content = result["content"] as! String
        
    }
    
    func switchDraft(id: String) {
        saveDraft()
        Task {
            do {
                try await queryDraftUseCase.invoke(id: id, version: nil).collect(collector: Collector<KotlinPair<NSString, DraftVersion>> { draft in
                    print(draft)
                    DispatchQueue.main.async {
                        self.currentDraftId = draft.first as? String ?? ""
                        self.title = draft.second?.title as? String ?? ""
                        self.content = draft.second?.content as? String ?? ""
                    }
                })
            } catch {
                print(error)
            }
        }
    }
    
    func postDraft() async{
        do{
            self.loading = true
            
            let result = try await postStoryUseCase.invoke(title: self.title, content: self.content, topic: self.selectedTopic, isPublished: self.isPublished, commentAllowed: self.commentAllowed, saveAllowed: self.saveAllowed)
            
            switch (result){
            case is StoryResultSuccess<KotlinUnit>:
                createNewDraft()
                self.postSuccess = true
                self.loading = false
                break
            default:
                self.error = result.errorMsg
                self.loading = false
                break
            }
        }catch{ print(error) }
    }
    
    func dismiss() {
        self.error = nil
        self.postSuccess = false
    }
}
