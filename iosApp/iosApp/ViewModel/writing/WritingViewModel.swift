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
    private let saveDraftUseCase = UseCasesHelper().saveDraftUseCase
    private let createNewDraftUseCase = UseCasesHelper().createNewDraftUseCase
    private let queryDraftUseCase = UseCasesHelper().queryDraftUseCase
    private let postStoryUseCase = UseCasesHelper().postStoryUseCase
    
    var currentDraftId: String? = nil
    var title: String = ""
    var content: String = ""
    //        var draftList: Map<String, String> = mapOf()
    //        var topicList: List<String> = listOf()
    var selectedTopic: String = ""
    var isPublished: Bool = false
    var commentAllowed: Bool = false
    var saveAllowed: Bool = false
    var error: String? = nil
    var postSuccess: Bool = false
    var loading: Bool = false
    var isUserSignedIn: Bool = false
    
    var menuItemList: [String] = ["Clear", "Edit History", "New Draft"]
    
    init() {
        
    }
    
    func onMenuClicked(indentifier: String) async {
        switch indentifier {
        case "Clear":
            clearTitleAndContent()
        case "Edit History": break
        case "New Draft":
            await createNewDraft()
        default: break
        }
    }
    
    func setTitle(title: String) {
        self.title = title
    }
    
    func setContent(content: String){
        self.content = content
    }
    
    func clearTitleAndContent() {
        self.title = ""
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
    
    func saveDraft() async {
        do {
            try await saveDraftUseCase.invoke(id: self.currentDraftId, title: self.title, content: self.content)
        } catch{ print(error) }
    }
    
    func createNewDraft() async {
        do {
            await saveDraft()
            let result = createNewDraftUseCase.invoke()
            print(result)
        }catch{ print(error) }
    }
    
    func switchDraft(id: String) async{
        do{
            await saveDraft()
            queryDraftUseCase.invoke(id: id)
        }catch{ print(error) }
    }
    
    func postDraft() async{
        do{
            self.loading = true
            
            let result = try await postStoryUseCase.invoke(title: self.title, content: self.content, topic: self.selectedTopic, isPublished: self.isPublished, commentAllowed: self.commentAllowed, saveAllowed: self.saveAllowed)
            
            switch (result){
            case is StoryResultSuccess<AnyObject>:
                await createNewDraft()
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
