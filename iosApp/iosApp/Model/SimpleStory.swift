//
//  Story.swift
//  iosApp
//
//  Created by Wah gor on 18/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

struct SimpleStory: Identifiable {
    let id: String
    let topic: String
    let title: String
    let bodyText: String
    
    init(id: String = UUID().uuidString, title: String, bodyText: String, topic: String = "") {
        self.id = id
        self.title = title
        self.bodyText = bodyText
        self.topic = topic
    }
}


extension SimpleStory {
    static var sampleData: [SimpleStory] {
        [
            SimpleStory(title: "Title1", bodyText: "Body"),
            SimpleStory(title: "Title2", bodyText: "Body"),
            SimpleStory(title: "Title3", bodyText: "Body"),
            SimpleStory(title: "Title4", bodyText: "Body"),
            SimpleStory(title: "Title5", bodyText: "Body"),
            SimpleStory(title: "Title6", bodyText: "Body"),
            SimpleStory(title: "Title7", bodyText: "Body")
        ]
    }
}
