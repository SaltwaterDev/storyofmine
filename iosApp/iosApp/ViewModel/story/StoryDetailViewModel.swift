//
//  StoryDetailViewModel.swift
//  iosApp
//
//  Created by Wah gor on 22/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//


import Foundation
import shared

@MainActor
class StoryDetailViewModel: ObservableObject {
    private let fetchStoryDetailUseCase = UseCasesHelper().fetchStoryDetailUseCase
    
    @Published var title: String = "Title"
    @Published var body: String = "Body"
    @Published var authorId: String = ""
    @Published var topic: String = "Topic"
    @Published var createdDate: String = ""
    @Published var isSelfWritten: Bool = false
    @Published var allowComment: Bool = false
    @Published var allowSave: Bool = false
    @Published var errorMsg: String? = nil
    @Published var loading: Bool = false

    
    func getStoryDetail(pid: String) async {
        do{
            loading = true
            let result = try await fetchStoryDetailUseCase.invoke(id: pid)
            switch (result) {
                case is StoryResultSuccess<Story>:
                    if let story = result.data {
                        title = story.title
                        body = story.content
                        authorId = story.author
                        topic = story.topic
                        createdDate = story.createdDate
                        allowComment = story.commentAllowed
                        allowSave = story.saveAllowed
                        isSelfWritten = story.isSelfWritten
                    }
                    
                default: errorMsg = result.errorMsg
            }
            loading = false
        } catch{
            print(error)
        }
    }
    
    func dismissError() {
        errorMsg = nil
    }
}
