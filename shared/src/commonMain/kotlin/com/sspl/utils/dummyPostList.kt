package com.sspl.utils

import com.sspl.core.models.Post

val dummyPostList = listOf(
    Post(
        postId = 1,
        userName = "John Doe",
        userProfileUrl = "https://example.com/john_doe.jpg",
        description = "Enjoying a beautiful day! Enjoying a beautiful day! Enjoying a beautiful day!",
        images = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
        likes = 100,
        dislikes = 5,
        commentCount = 20,
        disLikeCount = 5
    ),
    Post(
        postId = 2,
        userName = "Jane Smith",
        userProfileUrl = "https://example.com/jane_smith.jpg",
        description = "Weekend vibes!\n" +
                "Enjoying a beautiful day!\n" +
                "Enjoying a beautiful day!",
        images = listOf("https://example.com/image3.jpg"),
        likes = 50,
        dislikes = 2,
        commentCount = 15,
        disLikeCount = 2
    ),
    Post(
        postId = 3,
        userName = "JaneSmith",
        userProfileUrl = "https://example.com/jane_smith.jpg",
        description = "Hello Lorem Is not Just a text Hello \n" +
                "Enjoying a beautiful day!Lorem Is not Just a text Hello Lorem Is not Just a text",
        images = listOf(),
        likes = 50,
        dislikes = 2,
        commentCount = 15,
        disLikeCount = 2
    ),
    Post(
        postId = 4,
        userName = "AwesomeUser",
        userProfileUrl = "https://example.com/awesome_user.jpg",
        description = "Just chilling...\n" +
                "Enjoying a beautiful day!",
        images = emptyList(),
        likes = 200,
        dislikes = 10,
        commentCount = 30,
        disLikeCount = 10
    ),
    // New dummy data
    Post(
        postId = 5,
        userName = "MarkTwain",
        userProfileUrl = "https://example.com/mark_twain.jpg",
        description = "Writing a\n" +
                "Enjoying a beautiful day! new chapter...",
        images = listOf("https://example.com/image4.jpg", "https://example.com/image5.jpg"),
        likes = 350,
        dislikes = 15,
        commentCount = 40,
        disLikeCount = 8
    ),
    Post(
        postId = 6,
        userName = "EmilyDavis",
        userProfileUrl = "https://example.com/emily_davis.jpg",
        description = "Springtime in the city!\n" +
                "Enjoying a beautiful day!",
        images = listOf("https://example.com/image6.jpg"),
        likes = 500,
        dislikes = 10,
        commentCount = 25,
        disLikeCount = 4
    ),
    Post(
        postId = 7,
        userName = "TechGuru",
        userProfileUrl = "https://example.com/tech_guru.jpg",
        description = "Exploring the\n" +
                "Enjoying a beautiful day! latest gadgets!",
        images = listOf("https://example.com/image7.jpg"),
        likes = 150,
        dislikes = 8,
        commentCount = 10,
        disLikeCount = 3
    ),
    Post(
        postId = 8,
        userName = "TravelBug",
        userProfileUrl = "https://example.com/travel_bug.jpg",
        description = "Adventure awaits!",
        images = listOf("https://example.com/image8.jpg", "https://example.com/image9.jpg"),
        likes = 700,
        dislikes = 20,
        commentCount = 60,
        disLikeCount = 15
    ),
    Post(
        postId = 9,
        userName = "FoodLover",
        userProfileUrl = "https://example.com/food_lover.jpg",
        description = "Feasting on delicious cuisine!",
        images = listOf("https://example.com/image10.jpg", "https://example.com/image11.jpg"),
        likes = 450,
        dislikes = 18,
        commentCount = 35,
        disLikeCount = 9
    ),
    Post(
        postId = 10,
        userName = "FitnessFreak",
        userProfileUrl = "https://example.com/fitness_freak.jpg",
        description = "Another day, another workout!",
        images = listOf("https://example.com/image12.jpg"),
        likes = 600,
        dislikes = 12,
        commentCount = 50,
        disLikeCount = 6
    ),
    Post(
        postId = 11,
        userName = "NatureLover",
        userProfileUrl = "https://example.com/nature_lover.jpg",
        description = "Embracing the beauty of nature.",
        images = listOf("https://example.com/image13.jpg", "https://example.com/image14.jpg"),
        likes = 400,
        dislikes = 25,
        commentCount = 45,
        disLikeCount = 12
    ),
    Post(
        postId = 12,
        userName = "PetParent",
        userProfileUrl = "https://example.com/pet_parent.jpg",
        description = "My little friend is growing up so fast!",
        images = listOf("https://example.com/image15.jpg"),
        likes = 250,
        dislikes = 7,
        commentCount = 28,
        disLikeCount = 5
    ),
    Post(
        postId = 13,
        userName = "MusicLover",
        userProfileUrl = "https://example.com/music_lover.jpg",
        description = "Listening to some calming tunes...",
        images = listOf("https://example.com/image16.jpg", "https://example.com/image17.jpg"),
        likes = 320,
        dislikes = 5,
        commentCount = 12,
        disLikeCount = 2
    ),
    Post(
        postId = 14,
        userName = "ArtFanatic",
        userProfileUrl = "https://example.com/art_fanatic.jpg",
        description = "Exploring modern art at the gallery.",
        images = listOf("https://example.com/image18.jpg"),
        likes = 180,
        dislikes = 9,
        commentCount = 22,
        disLikeCount = 7
    )
)
