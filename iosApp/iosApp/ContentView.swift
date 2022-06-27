import SwiftUI
import shared

struct ContentView: View {
	let greet = Greeting().greeting()

	var body: some View {
        TabView{
            WritingScreen().tabItem{Image(systemName: "pencil")
                Text("Write")}
            LoginEmailScreen().tabItem{Image(systemName: "plus")
                                Text("Lounge")}
//            StoriesScreen().tabItem{Image(systemName: "plus")
//                Text("Lounge")}
            ProfileScreen().tabItem{Image(systemName: "person.fill")
                Text("Profile")}
            
        }
		
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
