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
    @State private var showLogin = false
    @State private var showSignup = false
    
    var body: some View {
        VStack{
            if (storiesViewModel.isUserLoggedIn){
                NavigationView{
                    ScrollView {
                        Text("Hello \(storiesViewModel.username ?? "")")
                            .font(.largeTitle)
                        
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
                }
            } else {
                VStack(spacing: 20){
                    Text("Sign up to read other stories")
                        .font(.headline)
                    
                    Button("Sign Up", action: {
                        showSignup = true
                    }).sheet(isPresented: $showSignup, onDismiss: {
                        storiesViewModel.checkAuth()
                    }, content: {
                        SignUpScreen(showSignup: $showSignup)
                    })
            
                    
                    Button("Login Instead", action: {
                        showLogin = true
                    }).sheet(isPresented: $showLogin, onDismiss: {
                        storiesViewModel.checkAuth()
                    }, content: {
                        LoginEmailScreen(showLogin: $showLogin){
                            NavigationLink(destination: SignUpScreen(showSignup: $showSignup)){
                                Text("Switch to Sign Up")
                            }
                        }
                    })
                }
            }
            }.onAppear {
                storiesViewModel.checkAuth()
                print("Stories Screen: \(storiesViewModel.isUserLoggedIn)")
            }
    }
}

//struct StoriesScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        StoriesScreen()
//    }
//}
