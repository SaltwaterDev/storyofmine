//
//  StoriesViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 16/7/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

@MainActor
class StoriesViewModel: ObservableObject {
    private let authRepo = AuthRepositoryHelper().authRepo()
    private let fetchStoryItemsUseCase = UseCasesHelper().fetchStoryItemsUseCase
    

    var shouldReload: Bool = true
    @Published var loading: Bool = false
    @Published private(set) var errorMsg: String? = nil
    @Published private(set) var username: String? = nil
    @Published private(set) var storiesByTopics: [StoriesComponent.TopicStories] = []
    
    
    func getStoriesItems() async{
        do{
            let result = try await fetchStoryItemsUseCase.invoke(lastItemId: nil)
            print(result)
            self.storiesByTopics = result.map{
                StoriesComponent.TopicStories(
                    topic: $0.topic,
                    stories: $0.stories.map{ s in
                        SimpleStory(
                            id: s.id,
                            title: s.title,
                            bodyText: s.content
                        )
                    }
                )
            }
        } catch{ print(error) }
    }

    
    func getUserName() async {
        do{
            let getUsernameResponse = try await authRepo.getUsername()
            switch (getUsernameResponse){
                case is AuthResultAuthorized<NSString>:
                    if( getUsernameResponse.data != nil){
                        username = getUsernameResponse.data as String?
                    }
                default:
                    errorMsg = getUsernameResponse.errorMsg
            }
        }catch{
            print(error)
        }
    }

    func dismissError() {
        errorMsg = nil
    }
}
