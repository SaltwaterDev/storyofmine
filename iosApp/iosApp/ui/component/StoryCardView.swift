//
//  StoryCard.swift
//  iosApp
//
//  Created by Wah gor on 18/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct StoryCardView: View {
    let title: String
    let bodyText: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            Text(title)
                .font(.headline)
                .lineLimit(2)
                .multilineTextAlignment(.leading)
                .frame(maxWidth: .infinity, alignment: .leading)
            
            Text(bodyText)
                .font(.body)
                .lineLimit(4)
                .multilineTextAlignment(.leading)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
//        .frame(alignment: .topLeading)
        .padding()
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .stroke(.blue)
        )
    }
}

struct StoryCardCiew_Previews: PreviewProvider {
    static var previews: some View {
        StoryCardView(title: "Title", bodyText: "Body")
    }
}
