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
            ScrollView {
                Text(storyDetailViewModel.title)
                    .font(.largeTitle)
                    .redacted(reason: storyDetailViewModel.loading ? .placeholder : [])
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal)
                
                Text(storyDetailViewModel.createdDate)
                    .font(.caption)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                
                Text(storyDetailViewModel.body)
                    .font(.body)
                    .redacted(reason: storyDetailViewModel.loading ? .placeholder : [])
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal)
            }
        }
        .task {
            await storyDetailViewModel.getStoryDetail(pid: storyId)
        }
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Image(systemName: "bookmark")
                    .padding()
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                Image(systemName: "ellipsis")
                    
            }
        }
    }
}

struct StoryDetailScreen_Previews: PreviewProvider {
    static var previews: some View {
        StoryDetailScreen(storyId: "1234asdf")
    }
}
