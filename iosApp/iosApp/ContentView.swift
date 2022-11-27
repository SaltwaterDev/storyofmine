import SwiftUI
import shared

struct ContentView: View {
    @StateObject var authSetting = AuthViewModel()
    @State private var tabSelection = 2
    @State private var postSuccessStory: String? = nil
    
	var body: some View {
        TabView(selection: $tabSelection){
            WritingScreen(tabSelection: $tabSelection, postSuccessStory: $postSuccessStory).tabItem{Image(systemName: "pencil.and.outline")
                    Text("Write")}.tag(1)
                StoriesScreen(postSuccessStory: $postSuccessStory).tabItem{Image(systemName: "book")
                    Text("Stories")}.tag(2)
                ProfileScreen().tabItem{Image(systemName: "person")
                    Text("Profile")}.tag(3)
            }.environmentObject(authSetting)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
