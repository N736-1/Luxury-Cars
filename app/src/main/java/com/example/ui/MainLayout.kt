package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.AffiliateClickLog
import com.example.data.Car
import com.example.data.GarageItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.AffiliateViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

enum class AppTab(val title: String) {
    CATALOG("Collection"),
    GARAGE("Garage"),
    PERFORMANCE("Performance")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(viewModel: AffiliateViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(AppTab.CATALOG) }
    
    // UI state subscriptions
    val garageItems by viewModel.garageItems.collectAsState()
    val clicksLog by viewModel.clicksLog.collectAsState()
    val totalMSRP by viewModel.totalMSRP.collectAsState()
    val totalCommissions by viewModel.totalCommissions.collectAsState()
    
    // Detailed dialog state
    var selectedCarForDetails by remember { mutableStateOf<Car?>(null) }
    var showRedirectionDialog by remember { mutableStateOf<Car?>(null) }
    var showBatchRedirectionDialog by remember { mutableStateOf<Boolean>(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "AFFILIATE PORTAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = Color(0xFF737373)
                        )
                        Text(
                            text = "Luxury Auto",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = (-0.5).sp
                            ),
                            color = ChromeWhite
                        )
                    }
                },
                actions = {
                    // Custom Luxury Badge Garage Shortcut matching HTML exactly
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF171717))
                            .border(1.dp, ImmersiveBorderHighlight, CircleShape)
                            .clickable { currentTab = AppTab.GARAGE }
                            .testTag("top_garage_shortcut"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Garage,
                            contentDescription = "My Garage",
                            tint = if (currentTab == AppTab.GARAGE) LuxuryGold else Color(0xFFD4D4D4),
                            modifier = Modifier.size(22.dp)
                        )
                        if (garageItems.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .background(LuxuryGold, CircleShape)
                                    .border(2.dp, DeepObsidian, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = garageItems.size.toString(),
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepObsidian,
                    titleContentColor = ChromeWhite
                ),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DeepObsidian,
                tonalElevation = 0.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = ImmersiveBorder,
                    shape = RoundedCornerShape(0.dp)
                )
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.CATALOG,
                    onClick = { currentTab = AppTab.CATALOG },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppTab.CATALOG) Icons.Default.Explore else Icons.Outlined.Explore,
                            contentDescription = "Catalog",
                            modifier = Modifier.size(26.dp)
                        )
                    },
                    label = { Text("Catalog", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LuxuryGold,
                        selectedTextColor = LuxuryGold,
                        indicatorColor = Color.Transparent, // Minimal, clean no-pill look
                        unselectedIconColor = Color.White.copy(alpha = 0.3f),
                        unselectedTextColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("nav_tab_catalog")
                )
                
                NavigationBarItem(
                    selected = currentTab == AppTab.GARAGE,
                    onClick = { currentTab = AppTab.GARAGE },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppTab.GARAGE) Icons.Default.Garage else Icons.Outlined.Garage,
                            contentDescription = "My Garage",
                            modifier = Modifier.size(26.dp)
                        )
                    },
                    label = { Text("Garage", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LuxuryGold,
                        selectedTextColor = LuxuryGold,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.White.copy(alpha = 0.3f),
                        unselectedTextColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("nav_tab_garage")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.PERFORMANCE,
                    onClick = { currentTab = AppTab.PERFORMANCE },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppTab.PERFORMANCE) Icons.Default.Poll else Icons.Outlined.Poll,
                            contentDescription = "Analytics",
                            modifier = Modifier.size(26.dp)
                        )
                    },
                    label = { Text("Analytics", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LuxuryGold,
                        selectedTextColor = LuxuryGold,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.White.copy(alpha = 0.3f),
                        unselectedTextColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("nav_tab_performance")
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) togetherWith
                    fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    AppTab.CATALOG -> {
                        CatalogTabContent(
                            viewModel = viewModel,
                            onViewDetails = { car -> selectedCarForDetails = car },
                            onDirectInquire = { car -> showRedirectionDialog = car }
                        )
                    }
                    AppTab.GARAGE -> {
                        GarageTabContent(
                            viewModel = viewModel,
                            onInquireAll = { showBatchRedirectionDialog = true },
                            onDirectInquire = { car -> showRedirectionDialog = car },
                            onExploreCatalog = { currentTab = AppTab.CATALOG }
                        )
                    }
                    AppTab.PERFORMANCE -> {
                        PerformanceTabContent(
                            viewModel = viewModel
                        )
                    }
                }
            }
            
            // Popups & Dynamic Sheets
            selectedCarForDetails?.let { car ->
                CarDetailsDialog(
                    car = car,
                    viewModel = viewModel,
                    onDismiss = { selectedCarForDetails = null },
                    onInquire = {
                        selectedCarForDetails = null
                        showRedirectionDialog = car
                    }
                )
            }

            showRedirectionDialog?.let { car ->
                AffiliateRedirectFlow(
                    car = car,
                    onComplete = {
                        viewModel.logSingleAffiliateRedirect(car)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(car.affiliateUrl))
                        context.startActivity(intent)
                        showRedirectionDialog = null
                        Toast.makeText(context, "Redirecting to Dealership with Partner ID Ref: AFF_12345", Toast.LENGTH_LONG).show()
                    },
                    onDismiss = { showRedirectionDialog = null }
                )
            }

            if (showBatchRedirectionDialog) {
                MultiAffiliateRedirectFlow(
                    garageItems = garageItems,
                    onComplete = {
                        viewModel.logBatchAffiliateCheckout(garageItems)
                        val primaryReferralUrl = "https://www.mercedesbenzgreenwich.com/inventory/?aff_id=AFF_12345"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(primaryReferralUrl))
                        context.startActivity(intent)
                        showBatchRedirectionDialog = false
                        Toast.makeText(context, "Redirecting to Dealership inventory with complete Garage reference!", Toast.LENGTH_LONG).show()
                    },
                    onDismiss = { showBatchRedirectionDialog = false }
                )
            }
        }
    }
}

// ==========================================
// 1. Catalog Tab UI Component
// ==========================================

@Composable
fun CatalogTabContent(
    viewModel: AffiliateViewModel,
    onViewDetails: (Car) -> Unit,
    onDirectInquire: (Car) -> Unit
) {
    var filterState by remember { mutableStateOf("ALL") }
    val catalog = viewModel.catalog
    
    val filteredCatalog = remember(filterState) {
        when (filterState) {
            "NEW" -> catalog.filter { it.condition.lowercase() == "new" }
            "USED" -> catalog.filter { it.condition.lowercase() == "used" }
            else -> catalog
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Welcome and Header
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "FLIGHT INVENTORY",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = Color(0xFF737373) // neutral-500 equivalent style from HTML
        )
        Text(
            text = "Bespoke Grand Collection",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Light, // Light, sharp, executive tracking
                letterSpacing = (-0.5).sp
            ),
            color = ChromeWhite
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Premium Filter Tabs matching Immersive UI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("ALL", "NEW", "USED").forEach { option ->
                val isSelected = filterState == option
                Surface(
                    onClick = { filterState = option },
                    shape = RoundedCornerShape(16.dp), // Premium rounded shape
                    color = if (isSelected) LuxuryGold else Color(0x0DFFFFFF), // White 5% for unselected
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) LuxuryGold else ImmersiveBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("filter_tab_$option")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = if (isSelected) Color.Black else SilverSatin
                        )
                    }
                }
            }
        }

        // Car Cards Scrollable Responsive Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 340.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCatalog, key = { it.id }) { car ->
                CarCatalogCard(
                    car = car,
                    viewModel = viewModel,
                    onClick = { onViewDetails(car) },
                    onInquireNow = { onDirectInquire(car) }
                )
            }
        }
    }
}

@Composable
fun CarCatalogCard(
    car: Car,
    viewModel: AffiliateViewModel,
    onClick: () -> Unit,
    onInquireNow: () -> Unit
) {
    val isSavedInGarage by viewModel.isCarInGarage(car.id).collectAsState(initial = false)
    val cardBackgroundBrush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFF161616), Color(0xFF0F0F0F))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("car_card_${car.id}"),
        shape = RoundedCornerShape(32.dp), // Premium 32dp rounded corners
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, ImmersiveBorder)
    ) {
        Box(
            modifier = Modifier
                .background(cardBackgroundBrush)
                .drawBehind {
                    // Atmospheric upper-right Gold radial glow matching HTML design
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(LuxuryGold.copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(size.width, 0f),
                            radius = size.width * 0.65f
                        ),
                        radius = size.width * 0.65f,
                        center = Offset(size.width, 0f)
                    )
                }
        ) {
            Column {
                // Elegant Image Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    AsyncImage(
                        model = car.imageUrl,
                        contentDescription = car.fullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    )
                    
                    // Content Overlay Gradients
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )

                    // Condition Badge Top Left - styled pilled/glassmorphic white/10
                    Surface(
                        color = Color(0x22FFFFFF),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = car.condition.uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // MSRP Badge Bottom Right
                    Surface(
                        color = Color.Black.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "MSRP ${car.price}",
                            color = LuxuryGold,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Typography Details Panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = car.brand.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LuxuryGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        text = car.model,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Light, // Lightweight luxury header
                            color = ChromeWhite
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = car.description,
                        color = SilverSatin,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Inquire Now - Styled Solid White with Black text (Premium HTML style)
                        Button(
                            onClick = onInquireNow,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
							),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(50.dp)
                                .testTag("inquire_btn_${car.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Launch,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Black
                                )
                                Text("Inquire Now", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Garage Toggle - Styled Outlined with subtle trans-overlay
                        OutlinedButton(
                            onClick = {
                                if (isSavedInGarage) {
                                    viewModel.removeFromGarage(car.id)
                                } else {
                                    viewModel.addToGarage(car)
                                }
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSavedInGarage) RedCalipers.copy(alpha = 0.5f) else LuxuryGold.copy(alpha = 0.5f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isSavedInGarage) RedCalipers else LuxuryGold
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("garage_toggle_btn_${car.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSavedInGarage) Icons.Default.Garage else Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isSavedInGarage) "Parked" else "Add Garage",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. My Garage Tab UI Component
// ==========================================

@Composable
fun GarageTabContent(
    viewModel: AffiliateViewModel,
    onInquireAll: () -> Unit,
    onDirectInquire: (Car) -> Unit,
    onExploreCatalog: () -> Unit
) {
    val garageItems by viewModel.garageItems.collectAsState()
    val totalMSRP by viewModel.totalMSRP.collectAsState()

    // Calculated virtual commission rate (1.5% of overall garage valuation)
    val potentialCommission = totalMSRP * 0.015

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "CUSTOMER SHOWROOM",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = Color(0xFF737373)
        )
        Text(
            text = "Your Private Garage",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Light,
                letterSpacing = (-0.5).sp
            ),
            color = ChromeWhite
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (garageItems.isEmpty()) {
            EmptyGarageState(onExploreCatalog = onExploreCatalog)
        } else {
            // Aggregate Valuation Card - Styled with Immersive Dark Gradient & subtle borders
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, ImmersiveBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("garage_summary_card")
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF161616), Color(0xFF0F0F0F))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Garage Asset Valuation",
                                    color = Color(0xFF737373),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = NumberFormat.getCurrencyInstance(Locale.US).format(totalMSRP).replace(".00", ""),
                                    color = ChromeWhite,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            
                            // Performance Tag highlighting payout yield
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Mktg Commission Yield",
                                    color = LuxuryGold,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = NumberFormat.getCurrencyInstance(Locale.US).format(potentialCommission).replace(".00", ""),
                                    color = LuxuryGoldLight,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Proceed to Inquiry Button matching the premium HTML design exactly!
                        Button(
                            onClick = onInquireAll,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuxuryGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp) // 64dp matches the HTML h-16 exactly
                                .testTag("garage_checkout_btn")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        "GARAGE READY",
                                        color = Color.Black.copy(alpha = 0.7f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        "Proceed to Inquiry",
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "AFF_12345",
                                        color = Color.Black.copy(alpha = 0.6f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "This will generate commission leads of 1.5% for all listed autos.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF737373),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Garage items scrolling block
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inventory List (${garageItems.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ChromeWhite
                )
                TextButton(
                    onClick = { viewModel.clearGarage() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All", fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(garageItems, key = { it.id }) { item ->
                    GarageItemRow(
                        item = item,
                        onRemove = { viewModel.removeFromGarage(item.id) },
                        onInquire = {
                            val matchingCar = viewModel.catalog.find { it.id == item.id }
                            if (matchingCar != null) {
                                onDirectInquire(matchingCar)
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyGarageState(onExploreCatalog: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .alpha(0.3f)
                .padding(bottom = 16.dp)
        ) {
            val strokeColor = Color(0xFFD4AF37)
            val fillBrush = Brush.radialGradient(
                colors = listOf(strokeColor.copy(alpha = 0.4f), Color.Transparent),
                center = Offset(size.width / 2, size.height / 2)
            )
            drawCircle(brush = fillBrush, radius = size.width / 2)
            drawArc(
                color = strokeColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        Text(
            text = "Your Garage is Empty",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = ChromeWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create a customized collections wishlist of premium high-end supercars to manage pricing, valuations, and generate broker commissions.",
            style = MaterialTheme.typography.bodyMedium,
            color = SilverSatin,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onExploreCatalog,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("empty_explore_btn")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null)
                Text("Explore Collections", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GarageItemRow(
    item: GarageItem,
    onRemove: () -> Unit,
    onInquire: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("garage_item_${item.id}"),
        shape = RoundedCornerShape(20.dp), // rounded-[24px] equivalent
        colors = CardDefaults.cardColors(containerColor = Color(0x1B121212)), // bg-neutral-900/50
        border = BorderStroke(1.dp, ImmersiveBorder) // border-white/5
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail container matching bg-neutral-800 rounded-xl
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF171717))
                    .border(1.dp, ImmersiveBorder, RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.fullName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Condition label
                Text(
                    text = "${item.condition.uppercase()} • Premium Sourced",
                    color = Color.White.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.fullName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.2).sp
                    ),
                    color = ChromeWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.price,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = LuxuryGold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Buttons: Chevron arrow for inquiry, trash icon for removal
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove From Garage",
                        tint = RedCalipers.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Inquire chevron button matching HTML right chevron action button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(1.dp, ImmersiveBorderHighlight, CircleShape)
                        .clickable { onInquire() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Inquire Individual",
                        tint = LuxuryGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. Performance Tab UI Component
// ==========================================

@Composable
fun PerformanceTabContent(
    viewModel: AffiliateViewModel
) {
    val clicksLog by viewModel.clicksLog.collectAsState()
    val totalCommissions by viewModel.totalCommissions.collectAsState()

    val commissionGoal = 50000.0
    val progressFraction = (totalCommissions / commissionGoal).coerceIn(0.0, 1.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "AFFILIATE TELEMETRY",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = Color(0xFF737373)
        )
        Text(
            text = "Performance Dashboard",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Light,
                letterSpacing = (-0.5).sp
            ),
            color = ChromeWhite
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Performance Overview cards Row (Linear dark gradient backgrounds with Immersive borders)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val statsCardBrush = remember {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF161616), Color(0xFF0F0F0F))
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, ImmersiveBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(statsCardBrush)
                        .padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text("Total Commissions", color = Color(0xFF737373), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale.US).format(totalCommissions).replace(".00", ""),
                            color = LuxuryGold,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Est. 1.5% Yield Rate", color = Color(0x80FFFFFF), fontSize = 10.sp)
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, ImmersiveBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(statsCardBrush)
                        .padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text("Redirection Leads", color = Color(0xFF737373), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = clicksLog.size.toString(),
                            color = ChromeWhite,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Unique Clicks Logged", color = Color(0x80FFFFFF), fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analytics Graph Card - Styled with transparent Card, Immersive border, and dark gradient background box
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, ImmersiveBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF161616), Color(0xFF0F0F0F))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "Weekly Commissions Pipeline",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        color = ChromeWhite
                    )
                    Text(
                        text = "Goal progress: ${"%.1f".format(progressFraction * 100)}% of $50k Gold Standard Payout Target",
                        color = SilverSatin,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom Graphic drawing using system Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(Color(0xFF090909), RoundedCornerShape(16.dp))
                            .border(1.dp, ImmersiveBorder, RoundedCornerShape(16.dp))
                            .padding(8.dp)
                    ) {
                        PerformanceAnalyticsGraph(clicksLog)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Click Event Logs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Live Click Telemetry Log",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = ChromeWhite
            )
            if (clicksLog.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearAnalyticsLog() },
                    colors = ButtonDefaults.textButtonColors(contentColor = RedCalipers)
                ) {
                    Text("Clear Logs", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (clicksLog.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0x06FFFFFF), RoundedCornerShape(20.dp))
                    .border(1.dp, ImmersiveBorder, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No analytics tracked yet.\nTap 'Inquire Now' on inventory vehicles.",
                    color = SilverSatin.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(clicksLog, key = { it.id }) { log ->
                    ClickLogCard(log)
                }
            }
        }
    }
}

@Composable
fun PerformanceAnalyticsGraph(clicks: List<AffiliateClickLog>) {
    // Canvas graph that takes clicked commissions and draws relative points
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .testTag("commission_chart_canvas")
    ) {
        val width = size.width
        val height = size.height

        // Simulated benchmark values if no clicks exist
        val dataPoints = if (clicks.isEmpty()) {
            listOf(200.0, 500.0, 300.0, 800.0, 450.0, 1000.0, 1500.0)
        } else {
            // Group recently recorded click logs by time slice or simply show logs sequentially up to 8 max
            val sequentials = clicks.take(8).reversed()
            if (sequentials.size < 2) {
                listOf(0.0) + sequentials.map { it.commissionEarned }
            } else {
                sequentials.map { it.commissionEarned }
            }
        }

        val maxVal = (dataPoints.maxOrNull() ?: 1.0).coerceAtLeast(100.0)

        // Draw horizontal grid divider lines
        val lineSpacing = height / 4
        for (i in 1..3) {
            drawLine(
                color = Color.White.copy(alpha = 0.08f),
                start = Offset(0f, i * lineSpacing),
                end = Offset(width, i * lineSpacing),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Generate coordinates and draw graph line path
        if (dataPoints.size >= 2) {
            val stepX = width / (dataPoints.size - 1)
            val path = Path()
            val fillPath = Path()

            val startY = height - ((dataPoints[0] / maxVal) * (height - 20f)).toFloat()
            path.moveTo(0f, startY)
            fillPath.moveTo(0f, height)
            fillPath.lineTo(0f, startY)

            for (i in 1 until dataPoints.size) {
                val currentX = i * stepX
                val currentY = (height - ((dataPoints[i] / maxVal) * (height - 25f))).toFloat()
                path.lineTo(currentX, currentY)
                fillPath.lineTo(currentX, currentY)
                
                // Draw circular accent dots
                drawCircle(
                    color = Color(0xFFD4AF37),
                    radius = 4.dp.toPx(),
                    center = Offset(currentX, currentY)
                )
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            // Fill gradient effect under the path line
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFD4AF37).copy(alpha = 0.25f), Color.Transparent)
                )
            )

            // Primary stroke trace line
            drawPath(
                path = path,
                color = Color(0xFFD4AF37),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        } else if (dataPoints.size == 1) {
            // Safe fallback line for single logs
            val singleY = height - ((dataPoints[0] / maxVal) * (height - 30f)).toFloat()
            drawLine(
                color = Color(0xFFD4AF37),
                start = Offset(0f, singleY),
                end = Offset(width, singleY),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
fun ClickLogCard(log: AffiliateClickLog) {
    val formatter = remember { SimpleDateFormat("MM-dd HH:mm:ss", Locale.US) }
    val timeLabel = formatter.format(Date(log.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x1B121212)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ImmersiveBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = log.modelName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ChromeWhite
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Ref Code: ${log.affiliateId}",
                        color = Color(0xFF737373),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "•",
                        color = Color.White.copy(alpha = 0.2f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = timeLabel,
                        color = Color(0xFF737373),
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${NumberFormat.getCurrencyInstance(Locale.US).format(log.commissionEarned).replace(".00", "")}",
                    color = LuxuryGold,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "COM. COMMITTED",
                    color = LuxuryGoldLight,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// Dialogs & Redirect Sheets
// ==========================================

@Composable
fun CarDetailsDialog(
    car: Car,
    viewModel: AffiliateViewModel,
    onDismiss: () -> Unit,
    onInquire: () -> Unit
) {
    val isSavedInGarage by viewModel.isCarInGarage(car.id).collectAsState(initial = false)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
            border = BorderStroke(1.dp, ImmersiveBorder),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
                .testTag("vehicle_details_sheet")
        ) {
            Column {
                // Feature Header Hero Image
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)) {
                    AsyncImage(
                        model = car.imageUrl,
                        contentDescription = car.fullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                    )
                    Text(
                        text = car.fullName,
                        color = ChromeWhite,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Light, // Elegant thin-weighted luxury typeface
                            letterSpacing = (-0.5).sp
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp)
                ) {
                    item {
                        Surface(
                            color = Color(0x11FFFFFF),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, ImmersiveBorder),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "MSRP PRICE: ${car.price}  |  CONDITION: ${car.condition.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = LuxuryGold,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        
                        Text(
                            text = "Vehicle Overview",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            color = ChromeWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = car.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SilverSatin
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = "Technical Specifications",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            color = ChromeWhite
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(car.specs.toList()) { (specName, specVal) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color(0xFF1C1C1C), RoundedCornerShape(12.dp))
                                .border(1.dp, ImmersiveBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(specName, color = SilverSatin, style = MaterialTheme.typography.bodyMedium)
                            Text(specVal, color = LuxuryGold, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Interactive Bottom buttons inside details modal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F0F0F))
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (isSavedInGarage) {
                                viewModel.removeFromGarage(car.id)
                            } else {
                                viewModel.addToGarage(car)
                            }
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isSavedInGarage) RedCalipers.copy(alpha = 0.5f) else LuxuryGold.copy(alpha = 0.5f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isSavedInGarage) RedCalipers else LuxuryGold
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (isSavedInGarage) "Parked" else "Add Garage",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onInquire,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Text("Inquire Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AffiliateRedirectFlow(
    car: Car,
    onComplete: () -> Unit,
    onDismiss: () -> Unit
) {
    // Elegant countdown loader dialog illustrating security routing
    var secsLeft by remember { mutableStateOf(3) }
    
    LaunchedEffect(Unit) {
        while (secsLeft > 0) {
            delay(800)
            secsLeft--
        }
        onComplete()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("redirect_flow_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(18.dp))
                
                Text(
                    text = "GENERATING AFFILIATE ROUTE",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = car.fullName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ChromeWhite,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Embedding Affiliate Tracker Token (ref: AFF_12345). Securely logging referral payload to campaign database. Redirecting in $secsLeft seconds...",
                    color = SilverSatin,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(18.dp))
                LinearProgressIndicator(
                    progress = { (3 - secsLeft) / 3f },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = DeepObsidian,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
fun MultiAffiliateRedirectFlow(
    garageItems: List<GarageItem>,
    onComplete: () -> Unit,
    onDismiss: () -> Unit
) {
    var secsLeft by remember { mutableStateOf(3) }
    val sumYield = garageItems.sumOf { it.numericPrice } * 0.015

    LaunchedEffect(Unit) {
        while (secsLeft > 0) {
            delay(800)
            secsLeft--
        }
        onComplete()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("batch_redirect_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "BATCH REFERRAL CHECKOUT",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${garageItems.size} Premium Vehicles Selected",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ChromeWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Total Projected Yield: ${NumberFormat.getCurrencyInstance(Locale.US).format(sumYield).replace(".00", "")}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Packaging multiple customer interest briefs with referral IDs. Logging all clicks concurrently to performance ledger. Redirecting in $secsLeft...",
                    color = SilverSatin,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))
                LinearProgressIndicator(
                    progress = { (3 - secsLeft) / 3f },
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = DeepObsidian,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

suspend fun delay(timeMs: Long) {
    kotlinx.coroutines.delay(timeMs)
}
