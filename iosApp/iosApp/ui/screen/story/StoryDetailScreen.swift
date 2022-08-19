//
//  StoryDetailScreen.swift
//  iosApp
//
//  Created by Wah gor on 19/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct StoryDetailScreen: View {
    let storyId: String
    
    var body: some View {
        Text(/*@START_MENU_TOKEN@*/"Hello, World!"/*@END_MENU_TOKEN@*/)
        Text("Story Id: \(storyId)")
    }
}

struct StoryDetailScreen_Previews: PreviewProvider {
    static var previews: some View {
        StoryDetailScreen(storyId: "1234asdf")
    }
}
