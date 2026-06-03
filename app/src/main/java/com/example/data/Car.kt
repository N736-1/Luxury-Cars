package com.example.data

data class Car(
    val id: Int,
    val brand: String,
    val model: String,
    val year: Int,
    val condition: String, // "New" or "Used"
    val price: String, // Format: "$114,500"
    val numericPrice: Double, // 114500.0
    val imageUrl: String,
    val affiliateUrl: String,
    val description: String,
    val specs: Map<String, String>
) {
    val fullName: String get() = "$year $brand $model"
}

object CarCatalog {
    val items = listOf(
        Car(
            id = 1,
            brand = "Mercedes-Benz",
            model = "S-Class",
            year = 2024,
            condition = "New",
            price = "$114,500",
            numericPrice = 114500.0,
            imageUrl = "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=800&auto=format&fit=crop&q=80",
            affiliateUrl = "https://www.mercedesbenzgreenwich.com/inventory/?model=S-Class&condition=New",
            description = "The pinnacle of executive luxury. The Mercedes-Benz S-Class defines the standard of dynamic elegance, bespoke digital cockpits, and unmatched ride refinement.",
            specs = mapOf(
                "Engine" to "3.0L Inline-6 Turbo with Mild Hybrid",
                "Horsepower" to "429 hp",
                "0-60 mph" to "4.8 seconds",
                "Transmission" to "9-Speed Automatic",
                "Drivetrain" to "4MATIC® All-Wheel Drive"
            )
        ),
        Car(
            id = 2,
            brand = "Mercedes-Benz",
            model = "G-Class",
            year = 2019,
            condition = "Used",
            price = "$130,000",
            numericPrice = 130000.0,
            imageUrl = "https://images.unsplash.com/photo-1520050206274-a1ae446cb3cc?w=800&auto=format&fit=crop&q=80",
            affiliateUrl = "https://www.mercedesbenzgreenwich.com/inventory/?model=G-Class&condition=Used",
            description = "An iconic design that defies time. This meticulously maintained pre-owned G-Class combines world-renowned off-road supremacy with legendary road presence.",
            specs = mapOf(
                "Engine" to "4.0L V8 Twin-Turbo",
                "Horsepower" to "416 hp",
                "0-60 mph" to "5.6 seconds",
                "Transmission" to "9-Speed Automatic",
                "Mileage" to "14,200 miles"
            )
        ),
        Car(
            id = 3,
            brand = "Porsche",
            model = "911 GT3",
            year = 2023,
            condition = "New",
            price = "$182,900",
            numericPrice = 182900.0,
            imageUrl = "https://images.unsplash.com/photo-1614162692292-7ac56d7f7f1e?w=800&auto=format&fit=crop&q=80",
            affiliateUrl = "https://www.porsche.com/usa/models/911/911-gt3-models/911-gt3/",
            description = "A thoroughbred track instrument designed for everyday roads. Featuring a high-revving naturally aspirated flat-six and advanced motorsport aerodynamics.",
            specs = mapOf(
                "Engine" to "4.0L Naturally Aspirated Flat-6",
                "Horsepower" to "502 hp",
                "0-60 mph" to "3.2 seconds",
                "Redline" to "9,000 RPM",
                "Transmission" to "7-Speed PDK"
            )
        ),
        Car(
            id = 4,
            brand = "Bentley",
            model = "Continental GT",
            year = 2021,
            condition = "Used",
            price = "$215,000",
            numericPrice = 215000.0,
            imageUrl = "https://images.unsplash.com/photo-1621135802920-133df287f89c?w=800&auto=format&fit=crop&q=80",
            affiliateUrl = "https://www.bentleymotors.com/en/models/continental.html",
            description = "The ultimate Grand Tourer. Hand-stitched leather, exquisite real veneer dashboards, and effortless, endless power for cross-continental speed.",
            specs = mapOf(
                "Engine" to "6.0L Twin-Turbocharged W12",
                "Horsepower" to "626 hp",
                "0-60 mph" to "3.6 seconds",
                "Key Feature" to "Rotary Display & Naim Audio",
                "Mileage" to "8,500 miles"
            )
        ),
        Car(
            id = 5,
            brand = "Audi",
            model = "R8 V10 Performance",
            year = 2023,
            condition = "New",
            price = "$158,600",
            numericPrice = 158600.0,
            imageUrl = "https://images.unsplash.com/photo-1603584173870-7f23fdae1b7a?w=800&auto=format&fit=crop&q=80",
            affiliateUrl = "https://www.audiusa.com/us/web/en/models/r8/r8-coupe/summary.html",
            description = "A dramatic symphony of naturally aspirated power. The iconic R8 embodies motorsport engineering, sharing over 50% of its parts with racing GT3 cars.",
            specs = mapOf(
                "Engine" to "5.2L Naturally Aspirated V10",
                "Horsepower" to "562 hp",
                "0-60 mph" to "3.4 seconds",
                "Transmission" to "7-Speed S-tronic Dual-Clutch",
                "Chassis" to "Audi Space Frame (ASF) Aluminum"
            )
        )
    )
}
