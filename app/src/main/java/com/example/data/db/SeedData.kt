package com.example.data.db

object SeedData {
    val initialUsers = listOf(
        UserEntity(
            id = "user_local_self",
            handle = "@rowan_earthcraft",
            displayName = "Rowan Meadow (You)",
            nodeAddress = "node://terra.p2p.rowan.8f49a",
            bioRegion = "Suncrest Valley / Cascadia Node",
            headline = "Homestead Grower, Regenerative Carpenter & Seed Exchanger",
            bio = "Practicing low-impact ecological living on 2 acres. Focused on cooperative barter and freeing our neighborhood from corporate middle-men. I measure true wealth in community trust, healthy soil, and shared tools.",
            earthPledge = "I always ask permission before taking from the land. Never spraying artificial chemicals. Respecting seasonal rhythms and honoring the earth spirit in every build.",
            termsAndConditions = "100% open to pure reciprocal barter! Happy to trade for organic produce, firewood, or mechanical help. Fair exchange based on mutual respect rather than currency.",
            ratingAverage = 4.95f,
            reviewCount = 14,
            tradesCompleted = 14,
            isLocalNodeUser = true,
            avatarColorHex = 0xFF2E6B34,
            joinedYear = "2024"
        ),
        UserEntity(
            id = "user_maya_grower",
            handle = "@solargrower_maya",
            displayName = "Maya Lin",
            nodeAddress = "node://terra.p2p.maya.3a91c",
            bioRegion = "Cascadia Bio-Region / Valley Creek",
            headline = "Heirloom Seed Keeper, Permaculture Food Forest & Organic Grower",
            bio = "Cultivating a 1.5 acre no-till perennial food forest. Specializing in rare heirloom squash, sweet berries, medicinal roots, and cold-hardy greens. Willing to deliver or host on-farm barter exchanges.",
            earthPledge = "Deep relationship with the soil microbiome. We never force yields with toxic fertilizers; we listen when Mother Earth signals a change in crop rotations.",
            termsAndConditions = "Direct barter preferred! Looking for seasoned firewood, welded trellis repair, or sourdough cultures. For community members without barter goods, sliding scale community tokens or $10/basket.",
            ratingAverage = 5.0f,
            reviewCount = 31,
            tradesCompleted = 34,
            isLocalNodeUser = false,
            avatarColorHex = 0xFF4A8C52,
            joinedYear = "2023"
        ),
        UserEntity(
            id = "user_jasper_solar",
            handle = "@renewable_jasper",
            displayName = "Jasper Vance",
            nodeAddress = "node://terra.p2p.jasper.77b4f",
            bioRegion = "High Desert Sun Basin / Node 12",
            headline = "Renewable Energy Specialist, Off-Grid Solar & Battery Restoration",
            bio = "Electrical engineer turned community sovereignty technician. Helping neighbors disconnect from polluting centralized grids. I design micro-hydro, solar photovoltaic rigs, and recover deep-cycle lithium cells.",
            earthPledge = "Committed to zero toxic runoff and sustainable energy autonomy. Working in harmony with solar flux rather than consuming dirty fossil fuels.",
            termsAndConditions = "Trades accepted: Organic pantry stores, fresh eggs, honey, or welding assistance. Cash only if custom high-grade parts must be acquired wholesale.",
            ratingAverage = 4.92f,
            reviewCount = 23,
            tradesCompleted = 26,
            isLocalNodeUser = false,
            avatarColorHex = 0xFFBA5D2C,
            joinedYear = "2023"
        ),
        UserEntity(
            id = "user_elena_apothecary",
            handle = "@herbal_elena",
            displayName = "Elena Solis",
            nodeAddress = "node://terra.p2p.elena.10f82",
            bioRegion = "Redwood Coastal Node / Mist Valley",
            headline = "Cold-Process Organic Soap Maker & Herbalism Teacher",
            bio = "Crafting wildcrafted botanical soaps using rain-collected water, organic olive oil, and ethically gathered yarrow and calendula. Hosting seasonal workshops on herbal self-reliance.",
            earthPledge = "Honoring plant spirits and harvesting only one-third of any wild stand to ensure robust regrowth for local wildlife.",
            termsAndConditions = "Barter for hand-carved soap dishes, clean beeswax, glass mason jars, or herbal seeds. Transparent trade value matches 1 bar = roughly 2 lbs fresh fruit.",
            ratingAverage = 4.98f,
            reviewCount = 42,
            tradesCompleted = 45,
            isLocalNodeUser = false,
            avatarColorHex = 0xFF6B9B37,
            joinedYear = "2023"
        ),
        UserEntity(
            id = "user_liam_wrench",
            handle = "@local_fixer_liam",
            displayName = "Liam 'Wrench' Keller",
            nodeAddress = "node://terra.p2p.liam.44e31",
            bioRegion = "Oak Ridge Foothills Node",
            headline = "Master Fixer, Small Engine & Sustainable Vehicle Converter",
            bio = "Anti-planned obsolescence advocate! I fix everything from tillers and chainsaws to vintage diesel trucks running on filtered waste vegetable oil. Also build cargo bike trailers.",
            earthPledge = "Repair is an act of love for Mother Earth. Every motor refurbished is one less chunk of metal mined from her crust.",
            termsAndConditions = "Will trade labor for farm produce, home-brewed cider, or blacksmithing materials. You provide scrap metal or spare parts if available.",
            ratingAverage = 4.88f,
            reviewCount = 19,
            tradesCompleted = 22,
            isLocalNodeUser = false,
            avatarColorHex = 0xFFE59834,
            joinedYear = "2024"
        ),
        UserEntity(
            id = "user_sophia_healer",
            handle = "@earth_healer_sophia",
            displayName = "Sophia Thorne",
            nodeAddress = "node://terra.p2p.sophia.98c22",
            bioRegion = "Mountain Pine Node / Watershed 4",
            headline = "Holistic Somatic Healer & Earth Connection Educator",
            bio = "Offering nervous system regulation, forest bathing, and guided land listening sessions. Teaching people how to consult with the landscape before undertaking major construction.",
            earthPledge = "Devoted to deep listening with our living biosphere. Aligning human community design with the wisdom of old-growth root systems.",
            termsAndConditions = "Reciprocal trades welcomed: massage or bodywork exchange, firewood splitting, or homemade sourdough and preserves.",
            ratingAverage = 5.0f,
            reviewCount = 28,
            tradesCompleted = 30,
            isLocalNodeUser = false,
            avatarColorHex = 0xFF2E7D9E,
            joinedYear = "2023"
        ),
        UserEntity(
            id = "user_julian_water",
            handle = "@eco_plumber_julian",
            displayName = "Julian Rios",
            nodeAddress = "node://terra.p2p.julian.52d9a",
            bioRegion = "Riverbed Basin Node",
            headline = "Ecological Plumber: Greywater, Rainwater & Gravity Systems",
            bio = "Installing water-sovereignty infrastructures. Clean compost toilets, passive reed-bed water filters, rainwater cisterns, and low-pressure drip irrigation.",
            earthPledge = "Water is sacred life-blood. Ensuring not a single drop is wasted or contaminated with synthetic surfactants.",
            termsAndConditions = "Open to trading for plumbing fittings, nursery fruit trees, or carpentry labor.",
            ratingAverage = 4.95f,
            reviewCount = 16,
            tradesCompleted = 18,
            isLocalNodeUser = false,
            avatarColorHex = 0xFF356859,
            joinedYear = "2024"
        ),
        UserEntity(
            id = "user_tariq_driver",
            handle = "@green_transit_tariq",
            displayName = "Tariq Morales",
            nodeAddress = "node://terra.p2p.tariq.66c1b",
            bioRegion = "Valley Transit Hub & Greenway",
            headline = "Zero-Emission Community Hauler & Cargo Bike Driver",
            bio = "Providing direct peer-to-peer delivery between farmers and neighbors using heavy-duty electric cargo bikes and bio-fueled van. No corporate gig-economy algorithms!",
            earthPledge = "Minimizing carbon footprint with silent human-electric propulsion and shared localized logistics.",
            termsAndConditions = "Trade delivery services for fresh produce, warm meals, bike tire tubes, or fermented goods.",
            ratingAverage = 4.97f,
            reviewCount = 37,
            tradesCompleted = 41,
            isLocalNodeUser = false,
            avatarColorHex = 0xFF8A5A36,
            joinedYear = "2023"
        )
    )

    val initialSkills = listOf(
        SkillEntity(
            id = "skill_1",
            userId = "user_maya_grower",
            authorName = "Maya Lin",
            authorHandle = "@solargrower_maya",
            bioRegion = "Valley Creek",
            title = "Fresh Organic Heirloom Produce Crate (Weekly Harvest)",
            categoryName = "FARMING_PRODUCE",
            description = "Crisp rainbow chard, sweet sungold cherry tomatoes, heritage winter squash, fresh culinary herbs (rosemary, sage, thyme), and garlic braids. Harvested the morning of exchange.",
            tradePreference = "Trade for 2 cords split firewood, mechanic repair on rototiller, or honey.",
            isPureBarter = true,
            estimatedValue = "Pure Barter (approx $35 value)",
            tagsJoined = "Produce,Heirloom,Organic,Vegetables,Food"
        ),
        SkillEntity(
            id = "skill_2",
            userId = "user_jasper_solar",
            authorName = "Jasper Vance",
            authorHandle = "@renewable_jasper",
            bioRegion = "Sun Basin",
            title = "Off-Grid Solar Consultation & Inverter Setup",
            categoryName = "RENEWABLE_ENERGY",
            description = "Complete audit of your cabin or homestead electrical demands. Sizing battery banks, testing panel output, and safely wiring off-grid charge controllers.",
            tradePreference = "Trade for dried beans, canned fruits, lumber, or mechanical repair.",
            isPureBarter = false,
            estimatedValue = "Barter or $40/hr community rate",
            tagsJoined = "Solar,Renewable,OffGrid,Electricity,CleanEnergy"
        ),
        SkillEntity(
            id = "skill_3",
            userId = "user_elena_apothecary",
            authorName = "Elena Solis",
            authorHandle = "@herbal_elena",
            bioRegion = "Mist Valley",
            title = "Herbal Artisan Soaps & Calendula Healing Salves",
            categoryName = "ARTISAN_CRAFT",
            description = "Bundle of 4 hand-cured cold process soaps infused with organic lavender, nettle, and pine resin. Also includes a 2oz jar of soothing calendula burn/wound salve.",
            tradePreference = "Trade for glass jars, beeswax, sourdough bread, or wool yarn.",
            isPureBarter = true,
            estimatedValue = "Pure Barter (4 soaps + 1 salve)",
            tagsJoined = "Soap,Herbal,Artisan,Handmade,Skincare"
        ),
        SkillEntity(
            id = "skill_4",
            userId = "user_liam_wrench",
            authorName = "Liam 'Wrench' Keller",
            authorHandle = "@local_fixer_liam",
            bioRegion = "Oak Ridge",
            title = "Tool Sharpening & Small Motor Restoration",
            categoryName = "FIXERS_BUILDERS",
            description = "Razor-sharp convex edges on axes, knives, chisels, and mower blades. Carburetor rebuilds on generators, weed trimmers, or water pumps.",
            tradePreference = "Trade for farm fresh eggs, berry preserves, or blacksmith scrap iron.",
            isPureBarter = false,
            estimatedValue = "Barter or $20/tool",
            tagsJoined = "Repair,Tools,Fixer,Sharpening,Machinery"
        ),
        SkillEntity(
            id = "skill_5",
            userId = "user_sophia_healer",
            authorName = "Sophia Thorne",
            authorHandle = "@earth_healer_sophia",
            bioRegion = "Mountain Pine",
            title = "Land Listening & Regenerative Site Consultation",
            categoryName = "HEALERS_TEACHERS",
            description = "Walking your acreage with you to read water patterns, wind breaks, animal corridors, and natural soil indicators before you build or clear.",
            tradePreference = "Trade for masonry stone, compost tea, or fermented kefir.",
            isPureBarter = true,
            estimatedValue = "Pure Barter",
            tagsJoined = "Ecology,Consulting,Land,Permaculture,Teaching"
        ),
        SkillEntity(
            id = "skill_6",
            userId = "user_julian_water",
            authorName = "Julian Rios",
            authorHandle = "@eco_plumber_julian",
            bioRegion = "Riverbed Basin",
            title = "Rainwater Harvesting & Gravity Irrigation Build",
            categoryName = "PLUMBING_WATER",
            description = "Installing first-flush diverters, linking food-grade IBC totes, and setting up zero-electricity drip lines for your greenhouse or orchard.",
            tradePreference = "Trade for fruit tree saplings, weld repairs, or home-cooked feasts.",
            isPureBarter = false,
            estimatedValue = "Barter or $30/hr community rate",
            tagsJoined = "Water,Rainwater,Plumbing,Irrigation,OffGrid"
        ),
        SkillEntity(
            id = "skill_7",
            userId = "user_tariq_driver",
            authorName = "Tariq Morales",
            authorHandle = "@green_transit_tariq",
            bioRegion = "Valley Transit",
            title = "Eco Cargo Hauling & Neighbor-to-Neighbor Delivery",
            categoryName = "TRANSPORT_DELIVERY",
            description = "Moving up to 300 lbs of garden soil, lumber, harvest boxes, or heavy tools across the bio-region using high-capacity electric cargo bike and trailer.",
            tradePreference = "Trade for fresh baked bread, fruit preserves, or bicycle tune-ups.",
            isPureBarter = true,
            estimatedValue = "Pure Barter",
            tagsJoined = "Delivery,Transport,Hauling,CargoBike,CleanTransit"
        ),
        SkillEntity(
            id = "skill_8",
            userId = "user_local_self",
            authorName = "Rowan Meadow (You)",
            authorHandle = "@rowan_earthcraft",
            bioRegion = "Suncrest Valley",
            title = "Handcrafted Cedar Planter Boxes & Garden Tool Repair",
            categoryName = "FIXERS_BUILDERS",
            description = "Custom raised cedar planter beds crafted from sustainably salvaged local wood without chemical stains. Plus wooden tool handle replacements.",
            tradePreference = "Trade for berry starts, compost worms, or herbal tincture supplies.",
            isPureBarter = true,
            estimatedValue = "Pure Barter (Custom cedar box)",
            tagsJoined = "Woodworking,Planters,Garden,Carpentry"
        )
    )

    val initialWishlists = listOf(
        WishlistEntity(
            id = "wish_1",
            userId = "user_maya_grower",
            authorName = "Maya Lin",
            authorHandle = "@solargrower_maya",
            title = "Clean 55-Gal Food Grade Barrels for Fermenting Compost Tea",
            categoryName = "PLUMBING_WATER",
            urgency = "High Need",
            willExchangeFor = "2 Large Crates of Heirloom Tomatoes & Squash"
        ),
        WishlistEntity(
            id = "wish_2",
            userId = "user_jasper_solar",
            authorName = "Jasper Vance",
            authorHandle = "@renewable_jasper",
            title = "Rough-Cut 2x6 Douglas Fir Lumber (10-12 ft lengths)",
            categoryName = "FIXERS_BUILDERS",
            urgency = "Flexible",
            willExchangeFor = "Full Solar Inverter Diagnostic & Panel Wiring"
        ),
        WishlistEntity(
            id = "wish_3",
            userId = "user_elena_apothecary",
            authorName = "Elena Solis",
            authorHandle = "@herbal_elena",
            title = "Raw Local Beeswax & Half-Pint Mason Jars with Lids",
            categoryName = "ARTISAN_CRAFT",
            urgency = "Ongoing",
            willExchangeFor = "Cold-Process Lavender & Pine Soap Bars"
        ),
        WishlistEntity(
            id = "wish_4",
            userId = "user_liam_wrench",
            authorName = "Liam 'Wrench' Keller",
            authorHandle = "@local_fixer_liam",
            title = "Heavy-Duty Anvil or 50+ lb Steel Scrap for Metal Work",
            categoryName = "FIXERS_BUILDERS",
            urgency = "Flexible",
            willExchangeFor = "Full Small Engine Rebuild (Chainsaw or Rototiller)"
        ),
        WishlistEntity(
            id = "wish_5",
            userId = "user_local_self",
            authorName = "Rowan Meadow (You)",
            authorHandle = "@rowan_earthcraft",
            title = "Red Wiggler Composting Worms & Seed Garlic",
            categoryName = "FARMING_PRODUCE",
            urgency = "High Need",
            willExchangeFor = "Hand-carved cedar spoons or planter building"
        )
    )

    val initialTrades = listOf(
        TradeEntity(
            id = "trade_101",
            initiatorId = "user_local_self",
            initiatorName = "Rowan Meadow (You)",
            receiverId = "user_maya_grower",
            receiverName = "Maya Lin",
            offeredItemTitle = "Handcrafted Cedar Garden Bed",
            requestedItemTitle = "Heirloom Squash & Tomato Starts Crate",
            termsAgreed = "1 Raised cedar box delivered in exchange for 2 crates of healthy seedlings and companion herbs.",
            statusName = "COMPLETED",
            timestamp = System.currentTimeMillis() - (86400000L * 3),
            ratingScore = 5,
            reviewComment = "Wonderful trade! Maya's seedlings are thriving in my garden, soil packed with beneficial mycorrhizae. Honest and inspiring community neighbor.",
            stewardshipBadge = "Harm-Free Earth Trade"
        ),
        TradeEntity(
            id = "trade_102",
            initiatorId = "user_elena_apothecary",
            initiatorName = "Elena Solis",
            receiverId = "user_local_self",
            receiverName = "Rowan Meadow (You)",
            offeredItemTitle = "Bundle of 6 Botanical Herb Soaps",
            requestedItemTitle = "Cedar Hand-Carved Slotted Soap Trays",
            termsAgreed = "Direct barter: 6 soaps for 3 durable cedar soap dishes with natural linseed finish.",
            statusName = "ACCEPTED",
            timestamp = System.currentTimeMillis() - (86400000L * 1),
            ratingScore = null,
            reviewComment = null,
            stewardshipBadge = null
        ),
        TradeEntity(
            id = "trade_103",
            initiatorId = "user_jasper_solar",
            initiatorName = "Jasper Vance",
            receiverId = "user_liam_wrench",
            receiverName = "Liam 'Wrench' Keller",
            offeredItemTitle = "Solar Battery Bank Load Test",
            requestedItemTitle = "Welding Reinforcement on Solar Ground Mount",
            termsAgreed = "Equal skill exchange. Both parties verify safe connections.",
            statusName = "REVIEWED",
            timestamp = System.currentTimeMillis() - (86400000L * 6),
            ratingScore = 5,
            reviewComment = "Liam welded structural triangles on my array frame. Indestructible craftsmanship. Zero money exchanged, pure peer power.",
            stewardshipBadge = "Exceptional Craft & Integrity"
        )
    )

    val initialReferrals = listOf(
        ReferralEntity(
            id = "ref_1",
            referrerId = "user_sophia_healer",
            referrerName = "Sophia Thorne",
            introducedPersonId = "user_jasper_solar",
            introducedPersonName = "Jasper Vance (Renewable Energy)",
            seekerPersonId = "user_local_self",
            seekerPersonName = "Rowan Meadow (You)",
            serviceOrSkillFocus = "Off-grid Solar Inverter & Battery Bank",
            introNote = "Hi Rowan! You mentioned wanting to take your workshop off the grid. Jasper is our neighborhood solar wizard and happens to be looking for custom cedar carpentry. You two would have an incredible trade synergy!",
            timestamp = System.currentTimeMillis() - (86400000L * 2),
            isContacted = false
        ),
        ReferralEntity(
            id = "ref_2",
            referrerId = "user_maya_grower",
            referrerName = "Maya Lin",
            introducedPersonId = "user_tariq_driver",
            introducedPersonName = "Tariq Morales (Eco Hauler)",
            seekerPersonId = "user_elena_apothecary",
            seekerPersonName = "Elena Solis",
            serviceOrSkillFocus = "Cargo Bike Delivery of Apothecary Supplies",
            introNote = "Connecting Elena and Tariq: Tariq passes through Mist Valley on Tuesdays and can easily transport clean glass jars and soap crates!",
            timestamp = System.currentTimeMillis() - (86400000L * 4),
            isContacted = true
        )
    )

    val initialReviews = listOf(
        ReviewEntity(
            id = "rev_1",
            tradeId = "trade_past_1",
            reviewerId = "user_liam_wrench",
            reviewerName = "Liam Keller",
            targetUserId = "user_maya_grower",
            rating = 5,
            comment = "Maya's heirloom squash and garlic stored through the entire winter. She showed me her compost system and how she respects soil life. True community anchor.",
            stewardshipRating = "Deep Reverence for the Land",
            verifiedTrade = true,
            timestamp = System.currentTimeMillis() - (86400000L * 14)
        ),
        ReviewEntity(
            id = "rev_2",
            tradeId = "trade_past_2",
            reviewerId = "user_maya_grower",
            reviewerName = "Maya Lin",
            targetUserId = "user_jasper_solar",
            rating = 5,
            comment = "Jasper restored two discarded deep-cycle batteries from a neighbor's RV and wired them to our greenhouse fan. Worked seamlessly without buying new mining products.",
            stewardshipRating = "Circular & Waste-Free Energy",
            verifiedTrade = true,
            timestamp = System.currentTimeMillis() - (86400000L * 10)
        ),
        ReviewEntity(
            id = "rev_3",
            tradeId = "trade_past_3",
            reviewerId = "user_local_self",
            reviewerName = "Rowan Meadow",
            targetUserId = "user_elena_apothecary",
            rating = 5,
            comment = "The pine resin and yarrow soap healed working hands cracked from carpentry in two days. Gentle, completely zero synthetic fragrances.",
            stewardshipRating = "Pure Botanical Craftsmanship",
            verifiedTrade = true,
            timestamp = System.currentTimeMillis() - (86400000L * 7)
        )
    )

    val initialNodeInfo = NodeEntity(
        nodeId = "node_local_cascadia_7f8",
        nodeAlias = "TerraNode-Rowan-Cascadia",
        publicKeyFingerprint = "SHA256:7f89b9a4c01d4483aef1994e63b190df0e7b8c21a4f009e",
        localPort = 4289,
        isMeshSyncing = true,
        connectedPeersCount = 8,
        storageUsageKb = 1420L,
        earthStewardshipVerified = true
    )
}
