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
    
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            Text(title)
                .font(.headline)
                .lineLimit(2)
                .multilineTextAlignment(.leading)
                .frame(maxWidth: UIScreen.screenWidth - 60, alignment: .leading)
            
            Text(bodyText)
                .font(.body)
                .lineLimit(4)
                .multilineTextAlignment(.leading)
                .frame(maxWidth: UIScreen.screenWidth - 60, alignment: .leading)
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 8)
                .fill(Color.init(red: 255, green: 245, blue: 158))
                .opacity(1)
                .shadow(radius: 8)
        )
        
    }
}

struct StoryCardCiew_Previews: PreviewProvider {
    static var previews: some View {
        StoryCardView(title: "Title", bodyText: "Body")
    }
}
