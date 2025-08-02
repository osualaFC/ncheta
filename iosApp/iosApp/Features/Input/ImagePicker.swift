//
//  ImagePicker.swift
//  iosApp
//
//  Created by fredrick osuala on 2/8/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import PhotosUI

@MainActor
struct ImagePicker: UIViewControllerRepresentable {

    enum SourceType {
        case camera
        case photoLibrary
    }

    let sourceType: SourceType
    var onImagePicked: (Data?) -> Void

    @Environment(\.dismiss) private var dismiss

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    // ✅ Updated to return UIViewController
    func makeUIViewController(context: Context) -> UIViewController {
        switch sourceType {
        case .photoLibrary:
            var config = PHPickerConfiguration(photoLibrary: .shared())
            config.selectionLimit = 1
            config.filter = .images
            let picker = PHPickerViewController(configuration: config)
            picker.delegate = context.coordinator
            return picker

        case .camera:
            let picker = UIImagePickerController()
            picker.sourceType = .camera
            picker.delegate = context.coordinator
            return picker
        }
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    @MainActor
    class Coordinator: NSObject, PHPickerViewControllerDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

        let parent: ImagePicker

        init(parent: ImagePicker) {
            self.parent = parent
        }

        // 📷 Photo library
        func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
            parent.dismiss()

            guard let provider = results.first?.itemProvider else {
                parent.onImagePicked(nil)
                return
            }

            provider.loadObject(ofClass: UIImage.self) { image, error in
                if let error = error {
                    Task { @MainActor in
                        print("Error loading image: \(error)")
                        self.parent.onImagePicked(nil)
                    }
                    return
                }

                guard let uiImage = image as? UIImage else {
                    Task { @MainActor in self.parent.onImagePicked(nil) }
                    return
                }

                let imageData = uiImage.jpegData(compressionQuality: 0.8)
                Task { @MainActor in self.parent.onImagePicked(imageData) }
            }
        }

        // 📸 Camera
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            parent.dismiss()

            let image = info[.originalImage] as? UIImage
            let imageData = image?.jpegData(compressionQuality: 0.8)
            parent.onImagePicked(imageData)
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            parent.dismiss()
            parent.onImagePicked(nil)
        }
    }
}

