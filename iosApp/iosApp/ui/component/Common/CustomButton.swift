//
//  CustomButton.swift
//  iosApp
//
//  Created by Daniel Sau on 6/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct CustomButton: View {
    var title = ""
    let callback: () -> ()
    
    var body: some View {
        Button(action: {
            withAnimation {
                callback()
            }
        }) {
            Text(title)
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(6)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .fill(Color.init(red: 255, green: 245, blue: 158))
                        .opacity(1)
                )
        }
    }
}

struct CustomButton_Previews: PreviewProvider {
    static var previews: some View {
        CustomButton(title: "", callback: {})
    }
}
