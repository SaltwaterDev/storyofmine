//
//  TopicStories.swift
//  iosApp
//
//  Created by Wah gor on 18/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct TopicStoriesView: View {
    let topicStories: StoriesComponent.TopicStories
    
    var body: some View {
        VStack(alignment: .leading){
            Text(topicStories.topic)
                .padding(.leading)
                .font(.title)
            
            TabView{
                ForEach(topicStories.stories) {story in
                    VStack{
                        NavigationLink{
                            StoryDetailScreen(storyId: story.id)
                        } label: {
                            StoryCardView(
                                title: story.title, bodyText: story.bodyText
                            )
                        }
                        Spacer()
                    }
                }
                .padding(.horizontal, 10)
                .frame(alignment: .topLeading)
                    
            }
            .frame(height: 200)
            .tabViewStyle(PageTabViewStyle(indexDisplayMode: .automatic))
        }
    }
}

struct TopicStoriesView_Previews: PreviewProvider {
    static var previews: some View {
        TopicStoriesView(
            topicStories: StoriesComponent.TopicStories(
                topic: "Topic",
                stories: SimpleStory.sampleData
            )
        )
    }
}
