package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary

data class DeliveryOffer(
    val id: String,
    val title: String,
    val city: String,
    val district: String,
    val roleType: String, // "طلب توصيل" (Shop) or "عرض توصيل" (Driver)
    val phone: String,
    val timeAgo: String,
    val details: String,
    val vehicleType: String = "دراجة نارية"
)

data class DriverProfile(
    val id: String,
    val name: String,
    val city: String,
    val vehicle: String,
    val phone: String,
    val isAvailable: Boolean,
    val experience: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativeHomeScreenNew(
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var currentTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Offers, 2: Map, 3: Add, 4: Drivers
        var selectedRole by remember { mutableStateOf("صاحب محل") } // "صاحب محل" or "ليفرور"
        var selectedCity by remember { mutableStateOf("جميع مدن المغرب") }
        var showAddModal by remember { mutableStateOf(false) }
        var cityDropdownExpanded by remember { mutableStateOf(false) }

        // Initial sample offers across Moroccan cities
        val offersList = remember {
            mutableStateListOf(
                DeliveryOffer(
                    id = "1",
                    title = "محل إلكترونيات وأكسسوارات",
                    city = "الدار البيضاء",
                    district = "المعاريف / سيدي معروف",
                    roleType = "طلب توصيل",
                    phone = "0661001122",
                    timeAgo = "منذ 15 دقيقة",
                    details = "مطلوب ليفرور لتوصيل طلبيات يومية ثابتة من 10 صباحاً إلى 7 مساءً."
                ),
                DeliveryOffer(
                    id = "2",
                    title = "ليفرور بموتور خاص (متاح للتوصيل)",
                    city = "الرباط",
                    district = "أكدال / حسان / السويسي",
                    roleType = "عرض توصيل",
                    phone = "0670334455",
                    timeAgo = "منذ 30 دقيقة",
                    details = "سائق دراجة نارية سريع وموثوق متاح لتغطية طلبات المطاعم والمحلات بمدينة الرباط وسلا.",
                    vehicleType = "دراجة نارية"
                ),
                DeliveryOffer(
                    id = "3",
                    title = "مطعم ومأكولات سريعة",
                    city = "فاس",
                    district = "فاس الجديد / النرجس",
                    roleType = "طلب توصيل",
                    phone = "0652889900",
                    timeAgo = "منذ ساعة",
                    details = "نبحث عن سائقين لخدمة توصيل وجبات الغداء والعشاء بانتظام."
                ),
                DeliveryOffer(
                    id = "4",
                    title = "مكتبة ومستلزمات مكتبية",
                    city = "مراكش",
                    district = "جيليز / المحاميد",
                    roleType = "طلب توصيل",
                    phone = "0663112233",
                    timeAgo = "منذ ساعتين",
                    details = "توصيل طرود خفيفة وكتب للمدارس والشركات داخل مراكش."
                ),
                DeliveryOffer(
                    id = "5",
                    title = "ليفرور محترف بسيارة نفعية",
                    city = "طنجة",
                    district = "طنجة المدينة / بني مكادة",
                    roleType = "عرض توصيل",
                    phone = "0664778899",
                    timeAgo = "منذ 3 ساعات",
                    details = "مستعد لتوصيل الطرود المتوسطة والكبيرة في طنجة ونواحيها بكل أمانة.",
                    vehicleType = "سيارة نفعية"
                )
            )
        }

        val driversList = remember {
            mutableStateListOf(
                DriverProfile("d1", "يوسف العمراني", "الدار البيضاء", "دراجة نارية", "0661122334", true, "3 سنوات"),
                DriverProfile("d2", "كريم السبتي", "الرباط", "دراجة نارية", "0672233445", true, "سنتان"),
                DriverProfile("d3", "حمزة التازي", "فاس", "دراجة نارية", "0653344556", true, "4 سنوات"),
                DriverProfile("d4", "أنس البورقادي", "طنجة", "سيارة خفيفة", "0664455667", false, "5 سنوات"),
                DriverProfile("d5", "رشيد المرابط", "مراكش", "دراجة نارية", "0675566778", true, "سنة واحدة"),
                DriverProfile("d6", "طارق بنعلي", "أكادير", "دراجة نارية", "0656677889", true, "سنتان")
            )
        }

        if (showAddModal || currentTab == 3) {
            AddScreen(
                onDismiss = {
                    showAddModal = false
                    if (currentTab == 3) currentTab = 0
                },
                initialType = if (selectedRole == "صاحب محل") "طلب توصيل" else "عرض توصيل",
                onAddSubmit = { title, city, phone, details, type ->
                    offersList.add(
                        0,
                        DeliveryOffer(
                            id = System.currentTimeMillis().toString(),
                            title = title,
                            city = city,
                            district = "مركز المدينة",
                            roleType = type,
                            phone = phone,
                            timeAgo = "الآن",
                            details = details
                        )
                    )
                }
            )
            return@CompositionLocalProvider
        }

        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyPrimary)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Header row: Brand, Slogan, and Dark Mode button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RedPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "LV",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "LIVLINK",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "منصة ربط المحلات بالليفرورات",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Dark mode toggle
                        IconButton(
                            onClick = onToggleDarkMode,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NavySecondary)
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "تغيير الوضع الليلي / النهاري",
                                tint = if (isDarkMode) Color(0xFFFBBF24) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Nationwide Definition Banner
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = NavySecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = RedPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "منصة وطنية مغربية مجانية 100٪ بدون رسوم تغطي جميع مدن المملكة",
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Role Switcher & City Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Role Selector Pills
                        Row(
                            modifier = Modifier
                                .weight(1.1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavySecondary)
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selectedRole == "صاحب محل") RedPrimary else Color.Transparent)
                                    .clickable { selectedRole = "صاحب محل" }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "صاحب محل",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selectedRole == "ليفرور") RedPrimary else Color.Transparent)
                                    .clickable { selectedRole = "ليفرور" }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ليفرور",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // City Selector Dropdown
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavySecondary)
                                .clickable { cityDropdownExpanded = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = RedPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = selectedCity,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = cityDropdownExpanded,
                                onDismissRequest = { cityDropdownExpanded = false }
                            ) {
                                MOROCCAN_CITIES.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text(city) },
                                        onClick = {
                                            selectedCity = city
                                            cityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = NavyPrimary,
                    tonalElevation = 8.dp
                ) {
                    val navItems = listOf(
                        Triple("الرئيسية", Icons.Default.Home, 0),
                        Triple("العروض", Icons.Default.ListAlt, 1),
                        Triple("الخريطة", Icons.Default.Map, 2),
                        Triple("إضافة", Icons.Default.AddCircle, 3),
                        Triple("السائقون", Icons.Default.People, 4)
                    )

                    navItems.forEach { (label, icon, index) ->
                        NavigationBarItem(
                            selected = currentTab == index,
                            onClick = {
                                if (index == 3) {
                                    showAddModal = true
                                } else {
                                    currentTab = index
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (currentTab == index) RedPrimary else Color(0xFF94A3B8)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (currentTab == index) RedPrimary else Color(0xFF94A3B8)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RedPrimary,
                                selectedTextColor = RedPrimary,
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8),
                                indicatorColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentTab) {
                    0 -> HomeTabContent(
                        selectedRole = selectedRole,
                        selectedCity = selectedCity,
                        offers = offersList.filter {
                            selectedCity == "جميع مدن المغرب" || it.city == selectedCity
                        },
                        onNavigateToOffers = { currentTab = 1 },
                        onNavigateToMap = { currentTab = 2 },
                        onNavigateToDrivers = { currentTab = 4 },
                        onOpenAdd = { showAddModal = true }
                    )
                    1 -> OffersTabContent(
                        selectedCity = selectedCity,
                        offers = offersList.filter {
                            selectedCity == "جميع مدن المغرب" || it.city == selectedCity
                        },
                        onAddClick = { showAddModal = true }
                    )
                    2 -> MapTabContent(selectedCity = selectedCity)
                    4 -> DriversTabContent(
                        selectedCity = selectedCity,
                        drivers = driversList.filter {
                            selectedCity == "جميع مدن المغرب" || it.city == selectedCity
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTabContent(
    selectedRole: String,
    selectedCity: String,
    offers: List<DeliveryOffer>,
    onNavigateToOffers: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToDrivers: () -> Unit,
    onOpenAdd: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Action Hero Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (selectedRole == "صاحب محل") RedPrimary else NavyPrimary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedRole == "صاحب محل") "أهلاً بك يا صاحب المحل!" else "أهلاً بك يا بطل التوصيل (ليفرور)!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "مجاني 100٪",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedRole == "صاحب محل")
                            "انشر طلبات التوصيل الخاصة بمحلك وتواصل مباشرة مع ليفرورات مدينتك $selectedCity بدون أي وسيط وبدون اقتطاع سنتيم واحد."
                        else
                            "اعثر على محلات ومطاعم تبحث عن سائقين لتوصيل منتجاتها وتواصل معهم فوراً عبر الواتساب أو الهاتف مجاناً.",
                        fontSize = 13.sp,
                        color = Color(0xFFF1F5F9),
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onOpenAdd,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedRole == "صاحب محل") NavyPrimary else RedPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedRole == "صاحب محل") "نشر طلب توصيل جديد" else "نشر عرض خدمة توصيل",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Quick Service Links (Cards)
        item {
            Text(
                text = "الخدمات والوصول السريع:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Offers
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToOffers() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF2F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = null,
                                tint = RedPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تصفح العروض",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "طلبات التوصيل الحية",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Card 2: Drivers
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDrivers() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0FDF4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "دليل السائقين",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ليفرورات متاحون",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Card 3: Map
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToMap() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F9FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "خريطة المغرب",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "تغطية المدن",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Recent Offers Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "أحدث طلبات وعروض التوصيل ($selectedCity):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onNavigateToOffers) {
                    Text(
                        text = "عرض الكل",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedPrimary
                    )
                }
            }
        }

        if (offers.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد طلبات معروضة حالياً في $selectedCity",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(onClick = onOpenAdd) {
                            Text("كن أول من ينشر إعلاناً في هذه المدينة", color = RedPrimary)
                        }
                    }
                }
            }
        } else {
            items(offers.take(3)) { offer ->
                OfferCardItem(offer = offer)
            }
        }
    }
}

@Composable
fun OffersTabContent(
    selectedCity: String,
    offers: List<DeliveryOffer>,
    onAddClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "سوق العروض وطلبات التوصيل",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "المدينة المختارة: $selectedCity (${offers.size} إعلان)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = RedPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة")
                }
            }
        }

        if (offers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لم يتم العثور على عروض في $selectedCity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "يمكنك تغيير المدينة من القائمة العلوية أو نشر طلبك الآن مجاناً.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(offers) { offer ->
                OfferCardItem(offer = offer)
            }
        }
    }
}

@Composable
fun OfferCardItem(offer: DeliveryOffer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (offer.roleType == "طلب توصيل") Color(0xFFFEF2F2) else Color(0xFFEFF6FF))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = offer.roleType,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (offer.roleType == "طلب توصيل") RedPrimary else NavyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${offer.city} • ${offer.district}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = offer.timeAgo,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = offer.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = offer.details,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = offer.phone,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyPrimary
                    )
                }

                Button(
                    onClick = { /* Direct WhatsApp / Call intent */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "اتصال / واتساب",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MapTabContent(selectedCity: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "خريطة التغطية الوطنية — LIVLINK بالمغرب",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Map status card (honoring user instruction: do not claim external GPS service is active without config)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationSearching,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تغطية جغرافية وطنية نشطة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "LIVLINK تعمل على مستوى جميع جهات ومدن المملكة المغربية (الدار البيضاء، الرباط، فاس، طنجة، مراكش، أكادير، مكناس، وجدة وغيرها).",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Visual Map Representation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = NavySecondary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "المملكة المغربية",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "المدينة المختارة حالياً: $selectedCity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = RedPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ملاحظة تقنية: عرض خريطة Google Maps التفاعلية المباشرة يحتاج إلى تهيئة مفتاح Google Maps API للإنتاج، بينما تعمل المنصة حالياً عبر التوجيه المباشر بالمدن والأحياء وأرقام الاتصال.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF94A3B8),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DriversTabContent(
    selectedCity: String,
    drivers: List<DriverProfile>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "دليل السائقين المعتمدين في $selectedCity",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "تواصل مباشرة مع الليفرور المتاح في منطقتك بدون وسيط",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (drivers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "لا يوجد سائقون مسجلون حالياً في $selectedCity",
                        modifier = Modifier.padding(24.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(drivers) { driver ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (driver.isAvailable) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = if (driver.isAvailable) Color(0xFF16A34A) else Color.Gray,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = driver.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${driver.city} • ${driver.vehicle} (${driver.experience})",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (driver.isAvailable) "متاح حالياً للتوصيل" else "مشغول",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (driver.isAvailable) Color(0xFF16A34A) else Color.Gray
                                )
                            }
                        }

                        Button(
                            onClick = { /* WhatsApp / Call */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("اتصال", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
