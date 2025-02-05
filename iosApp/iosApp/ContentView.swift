import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .background(Color.purple)
            .ignoresSafeArea(edges: .all) // For IOS you need to disable safe area paddings being applied by the Switft UI
            .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}



