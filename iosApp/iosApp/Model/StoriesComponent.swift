//
//  StoriesComponent.swift
//  iosApp
//
//  Created by Wah gor on 19/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

enum StoriesComponent{
    struct TopicStories: Identifiable{
        let id: UUID
        let topic: String
        var stories: [Story]
        
        init(id: UUID = UUID(), topic: String, stories: [Story]) {
            self.id = id
            self.topic = topic
            self.stories = stories
        }
    }
}

extension StoriesComponent.TopicStories{
    static var sampleData: StoriesComponent.TopicStories{
        StoriesComponent.TopicStories(topic: "Title", stories: Story.sampleData)
    }
}
