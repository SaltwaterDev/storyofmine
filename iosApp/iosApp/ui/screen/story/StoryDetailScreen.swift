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
    @StateObject var storyDetailViewModel = StoryDetailViewModel()
    
    var body: some View {
        VStack(alignment: .leading){
            ScrollView{
                Text(storyDetailViewModel.title)
                    .font(.largeTitle)
                    .redacted(reason: storyDetailViewModel.loading ? .placeholder : [])
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                
                Text(storyDetailViewModel.body)
                    .font(.body)
                    .redacted(reason: storyDetailViewModel.loading ? .placeholder : [])
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
            }
        }
        .task {
            await storyDetailViewModel.getStoryDetail(pid: storyId)
        }
    }
}

struct StoryDetailScreen_Previews: PreviewProvider {
    static var previews: some View {
        StoryDetailScreen(storyId: "1234asdf")
    }
}
