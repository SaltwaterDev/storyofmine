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
    
    @Published private(set) var isUserLoggedIn: Bool = false
    @Published private(set) var loading: Bool = false
    @Published private(set) var errorMsg: String? = nil
    @Published private(set) var username: String? = nil
    @Published private(set) var storiesByTopics: [StoriesComponent.TopicStories] = []
    
    init(){
        loading = true
        Task{
            let result = try await authRepo.authenticate()
            print(result)
            switch (result){
                case is AuthResultAuthorized<KotlinUnit>:
                    print("Authorized")
                    await getUserName()
                    self.isUserLoggedIn = true
                    await getStoriesItems()
                
                case is AuthResultUnauthorized<KotlinUnit>:
                    print("Unauthorized")
                    self.isUserLoggedIn = false
                case is AuthResultUnknownError<KotlinUnit>:
                    print("Unknown error")
                    self.isUserLoggedIn = false
                    break
                default:
                    self.isUserLoggedIn = false
                    break
            }
            loading = false
        }
    }
    
    private func getStoriesItems() async{
        do{
            let result = try await fetchStoryItemsUseCase.invoke(lastItemId: nil)
            self.storiesByTopics = result.map{
                StoriesComponent.TopicStories(
                    topic: $0.topic,
                    stories: $0.stories.map{ s in
                        Story(title: s.title, bodyText: s.content)
                    }
                )
            }
        } catch{ print(error) }
    }

    
    private func getUserName() async {
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

    func checkAuth() {
        Task {
            let authResult = try await authRepo.authenticate()
            switch (authResult){
                case is AuthResultAuthorized<KotlinUnit>:
                    isUserLoggedIn = true
                case is AuthResultUnauthorized<KotlinUnit>:
                    isUserLoggedIn = false
                case is AuthResultUnknownError<KotlinUnit>:
                    isUserLoggedIn = false
                    let msg = authResult.errorMsg ?? ""
                    errorMsg = "Unknown error: " + msg
            default: break
            }
        }
    }
}
