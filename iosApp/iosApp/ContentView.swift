import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(edges: .all)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}


class MainViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Set the status bar style to dark content (black text/icons)
        setNeedsStatusBarAppearanceUpdate()

        // Additional setup for your Compose content can go here
    }

    // Override preferredStatusBarStyle to return dark content (black text/icons)
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    // If you want to dynamically change the status bar style, call this method
    func updateStatusBarStyle() {
        setNeedsStatusBarAppearanceUpdate()
    }
}
