//
//  MenuView.swift
//  iosApp
//
//  Created by Daniel Sau on 4/9/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct MenuView: View {
    var body: some View {
        VStack(alignment: .leading) {
            EmptyView().padding(.top, 100)
            MenuItem(title: "Clear")
            MenuItem(title: "Edit History")
            MenuItem(title: "New Draft")
            Spacer()
        }.padding()
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(red: 32/255, green: 32/255, blue: 32/255))
            .edgesIgnoringSafeArea(.all)
    }
}

struct MenuView_Previews: PreviewProvider {
    static var previews: some View {
        MenuView()
    }
}
