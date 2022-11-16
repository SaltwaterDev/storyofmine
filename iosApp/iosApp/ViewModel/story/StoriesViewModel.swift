//
//  StoriesViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 16/7/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared
import KMPNativeCoroutinesAsync

@MainActor
class StoriesViewModel: ObservableObject {
    private let authRepo = AuthRepositoryHelper().authRepo()
    private let fetchStoryItemsUseCase = UseCasesHelper().fetchStoryItemsUseCase
    var hasNextPage: Bool = false

    var shouldReload: Bool = true
    @Published var loading: Bool = false
    @Published private(set) var errorMsg: String? = nil
    @Published private(set) var username: String? = nil
    @Published private(set) var storyItems: [StoriesComponent.TopicStories] = []
    
    
    func getStoriesItems() async{
        do{
            loading = true
            let stream = asyncStream(for: fetchStoryItemsUseCase.pagingDataNative)
            
            for try await response in stream {
                print(response)
                self.storyItems = response.compactMap{
                    if let topicStories = $0 as? StoryItemStoriesByTopic{
                        return StoriesComponent.TopicStories(
                            topic: topicStories.topic,
                            stories: topicStories.stories.map{ s in
                                SimpleStory(
                                    id: s.id,
                                    title: s.title,
                                    bodyText: s.content
                                )
                            }
                        )
                    }
                    return nil
                }
            self.hasNextPage = self.fetchStoryItemsUseCase.pager.hasNextPage
            loading = false
            }
        } catch{
            print(error)
            loading = false
        }
    }

    
    func getUserName() async {
        do{
            let getUsernameResponse = try await authRepo.getUsername()
            switch (getUsernameResponse){
                case is AuthResultAuthorized<NSString>:
                    if(getUsernameResponse.data != nil){
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
    
    func fetchNextData() {
        fetchStoryItemsUseCase.pager.loadNext()
    }
    
    public var shouldDisplayNextPage: Bool {
        return hasNextPage
    }
}
