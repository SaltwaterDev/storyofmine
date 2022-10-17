//
//  SwiftUIView.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct StoriesScreen: View {
    @StateObject private var storiesViewModel = StoriesViewModel()
    @EnvironmentObject private var authSetting: AuthViewModel
    
    @State private var showLogin = false
    @State private var showSignup = false
    
    var body: some View {
        if (authSetting.isUserLoggedIn){
            storyView
        } else {
            loginRequiredVIew
        }
    }

    var storyView: some View{
        VStack {
            NavigationView{
                ScrollView {
                    Text("Hello \(storiesViewModel.username ?? "")")
                        .font(.largeTitle)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.all)
                    
                    ForEach(storiesViewModel.storiesByTopics){
                        TopicStoriesView(
                            topicStories: StoriesComponent.TopicStories(
                                topic: $0.topic,
                                stories: $0.stories
                            )
                        )
                    }
                }.redacted(reason: storiesViewModel.loading ? .placeholder : [])
                .navigationBarHidden(true)
                .frame(alignment: .leading)
                .refreshable {
                    print("refreshing...")
                    await self.initData()
                }
            }.task {
                if(storiesViewModel.shouldReload){
                    await self.initData()
                    storiesViewModel.shouldReload = false
                }
            }
        }
    }
    
    var loginRequiredVIew: some View{
        VStack(spacing: 20){
            Text("Sign up to read other stories")
                .font(.headline)
            
            Button("Sign Up", action: {
                showSignup = true
            }).sheet(
                isPresented: $showSignup,
                onDismiss: {
                    self.authSetting.authenticate()
                }, content: {
                    SignUpScreen(showSignup: $showSignup)
                })
    
            
            Button("Login Instead", action: {
                showLogin = true
            }).sheet(isPresented: $showLogin, onDismiss: {
                self.authSetting.authenticate()
            }, content: {
                LoginEmailScreen(showLogin: $showLogin){
                    NavigationLink(destination: SignUpScreen(showSignup: $showSignup)){
                        Text("Switch to Sign Up")
                    }
                }
            })
        }
    }
}

extension StoriesScreen {
    func initData() async {
        storiesViewModel.loading = true
        await self.storiesViewModel.getStoriesItems()
        await self.storiesViewModel.getUserName()
        storiesViewModel.loading = false
    }
}

//struct StoriesScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        StoriesScreen()
//    }
//}