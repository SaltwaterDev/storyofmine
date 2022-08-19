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
//    let loading: Boolean
    
    var body: some View {
        VStack(alignment: .leading){
            Text(topicStories.topic)
                .padding(.leading)
                .font(.title)
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 20) {
                    ForEach(topicStories.stories) {story in
                        NavigationLink{
                            StoryDetailScreen(storyId: story.id)
                        } label: {
                            StoryCardView(title: story.title, bodyText: story.bodyText)
                        }
                        
                    }
                }
                .padding()
            }
        }
    }
}

struct TopicStoriesView_Previews: PreviewProvider {
    static var previews: some View {
        TopicStoriesView(
            topicStories: StoriesComponent.TopicStories(
                topic: "Topic",
                stories: Story.sampleData
            )
//            loading: true
        )
    }
}
