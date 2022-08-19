//
//  Story.swift
//  iosApp
//
//  Created by Wah gor on 18/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

struct Story: Identifiable {
    let id: UUID
    let title: String
    let bodyText: String
    
    init(id: UUID = UUID(), title: String, bodyText: String) {
        self.id = id
        self.title = title
        self.bodyText = bodyText
    }
}


extension Story {
    static var sampleData: [Story] {
        [
            Story(title: "Title1", bodyText: "Body"),
            Story(title: "Title2", bodyText: "Body"),
            Story(title: "Title3", bodyText: "Body"),
            Story(title: "Title4", bodyText: "Body"),
            Story(title: "Title5", bodyText: "Body"),
            Story(title: "Title6", bodyText: "Body"),
            Story(title: "Title7", bodyText: "Body")
        ]
    }
}
