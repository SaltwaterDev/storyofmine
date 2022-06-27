//
//  SwiftUIView.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct WritingScreen: View {
    let greet = Greeting().greeting()
    
    var body: some View {
        Text(greet)
    }
}

struct WritingScreen_Previews: PreviewProvider {
    static var previews: some View {
        WritingScreen()
    }
}
