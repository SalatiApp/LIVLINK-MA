package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DriverOrangePrimary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.ShopBluePrimary

data class DeliveryOffer(
    val id: String,
    val title: String,
    val city: String,
    val district: String,
    val roleType: String, // "طلب توصيل" (Shop) or "عرض توصيل" (Driver)
    val phone: String,
    val timeAgo: String,
    val details: String,
    val vehicleType: String = "دراجة نارية",
    val priceEstimate: String = "حسب الاتفاق"
)

data class DriverProfile(
    val id: String,
    val name: String,
    val city: String,
    val vehicle: String,
    val phone: String,
    val isAvailable: Boolean,
    val experience: String,
    val completedTrips: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativeHomeScreenNew(
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
    currentRole: String = "صاحب محل",
    onRoleChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val isDriver = currentRole == "ليفرور"
    val activeColor = if (isDriver) DriverOrangePrimary else ShopBluePrimary

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        // Bottom nav tabs strictly in order:
        // 0: الرئيسية, 1: العروض, 2: إضافة (center), 3: الليفرورات, 4: حسابي
        var currentTab by remember { mutableIntStateOf(0) }
        var selectedCity by remember { mutableStateOf("جميع مدن المغرب") }
        var cityDropdownExpanded by remember { mutableStateOf(false) }
        var isDriverAvailable by remember { mutableStateOf(true) }

        // Selected offer for details dialog
        var selectedOfferForDetails by remember { mutableStateOf<DeliveryOffer?>(null) }
        var selectedDriverForDetails by remember { mutableStateOf<DriverProfile?>(null) }

        // Edit Profile Dialog state
        var showEditProfileDialog by remember { mutableStateOf(false) }
        var profileName by remember { mutableStateOf(if (isDriver) "سائق توصيل معتمد" else "محل تجاري - ليفلينك") }
        var profilePhone by remember { mutableStateOf("0661234567") }

        // Offers list
        val offersList = remember {
            mutableStateListOf(
                DeliveryOffer(
                    id = "1",
                    title = "محل إلكترونيات وأكسسوارات الهواتف",
                    city = "الدار البيضاء",
                    district = "المعاريف / سيدي معروف",
                    roleType = "طلب توصيل",
                    phone = "0661001122",
                    timeAgo = "منذ 10 دقائق",
                    details = "مطلوب ليفرور لتوصيل طلبيات هواتف وأكسسوارات داخل المعاريف وسيدي معروف يومياً.",
                    priceEstimate = "25 درهم / طلبية"
                ),
                DeliveryOffer(
                    id = "2",
                    title = "ليفرور بموتور خاص (توصيل سريع مع صندوق)",
                    city = "الرباط",
                    district = "أكدال / حسان / السويسي",
                    roleType = "عرض توصيل",
                    phone = "0670334455",
                    timeAgo = "منذ 25 دقيقة",
                    details = "سائق دراجة نارية بصندوق توصيل متاح لتغطية طلبات المطاعم والمحلات بمدينة الرباط وسلا ونواحيها.",
                    vehicleType = "دراجة نارية مع صندوق",
                    priceEstimate = "يومي أو بالطلبية"
                ),
                DeliveryOffer(
                    id = "3",
                    title = "مطعم وسناك مأكولات سريعة",
                    city = "فاس",
                    district = "فاس الجديد / النرجس",
                    roleType = "طلب توصيل",
                    phone = "0652889900",
                    timeAgo = "منذ 45 دقيقة",
                    details = "نبحث عن سائقين لخدمة توصيل وجبات الغداء والعشاء للمنازل والشركات بانتظام.",
                    priceEstimate = "20 درهم / توصيلة"
                ),
                DeliveryOffer(
                    id = "4",
                    title = "مكتبة ومستلزمات مدرسية وطباعة",
                    city = "مراكش",
                    district = "جيليز / المحاميد",
                    roleType = "طلب توصيل",
                    phone = "0663112233",
                    timeAgo = "منذ ساعتين",
                    details = "توصيل طرود كتب ومستلزمات مكتبية للمدارس والشركات بمراكش.",
                    priceEstimate = "30 درهم / إرسالية"
                ),
                DeliveryOffer(
                    id = "5",
                    title = "ليفرور بسيارة نفعية للبضائع والطرود",
                    city = "طنجة",
                    district = "طنجة المدينة / بني مكادة",
                    roleType = "عرض توصيل",
                    phone = "0664778899",
                    timeAgo = "منذ 3 ساعات",
                    details = "مستعد لتوصيل الطرود المتوسطة والكبيرة في طنجة والمنطقة الحرة بكل أمانة وسرعة.",
                    vehicleType = "سيارة نفعية",
                    priceEstimate = "حسب المسافة والوزن"
                ),
                DeliveryOffer(
                    id = "6",
                    title = "محل ملابس وأحذية رياضية",
                    city = "أكادير",
                    district = "تالبورجت / حي السلام",
                    roleType = "طلب توصيل",
                    phone = "0655667788",
                    timeAgo = "منذ 4 ساعات",
                    details = "توصيل طلبيات تجارة إلكترونية للزبائن في أكادير والدشيرة وإنزكان.",
                    priceEstimate = "25 درهم / طلبية"
                )
            )
        }

        val driversList = remember {
            mutableStateListOf(
                DriverProfile("d1", "يوسف العمراني", "الدار البيضاء", "دراجة نارية مع صندوق", "0661122334", true, "3 سنوات", 420),
                DriverProfile("d2", "كريم السبتي", "الرباط", "دراجة نارية مع صندوق", "0672233445", true, "سنتان", 280),
                DriverProfile("d3", "حمزة التازي", "فاس", "دراجة نارية", "0653344556", true, "4 سنوات", 510),
                DriverProfile("d4", "أنس البورقادي", "طنجة", "سيارة خفيفة", "0664455667", false, "5 سنوات", 630),
                DriverProfile("d5", "رشيد المرابط", "مراكش", "دراجة نارية مع صندوق", "0675566778", true, "سنة واحدة", 150),
                DriverProfile("d6", "طارق بنعلي", "أكادير", "دراجة نارية مع صندوق", "0656677889", true, "سنتان", 310)
            )
        }

        // Helper call functions with full error handling
        fun dialPhone(phoneNumber: String) {
            val clean = phoneNumber.replace(" ", "").replace("-", "")
            if (clean.isBlank()) {
                Toast.makeText(context, "رقم الهاتف غير متوفر", Toast.LENGTH_SHORT).show()
                return
            }
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean"))
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "رقم الاتصال: $clean", Toast.LENGTH_LONG).show()
            }
        }

        fun openWhatsApp(phoneNumber: String) {
            val clean = phoneNumber.replace(" ", "").replace("-", "")
            if (clean.isBlank()) {
                Toast.makeText(context, "رقم الهاتف غير متوفر", Toast.LENGTH_SHORT).show()
                return
            }
            val intPhone = if (clean.startsWith("0")) clean.substring(1) else clean
            val uri = Uri.parse("https://wa.me/212$intPhone")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                dialPhone(phoneNumber)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                Surface(
                    color = NavyPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        // Header row: Brand, Slogan, and Reorganized Top Bar Icons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Brand Logo and Title
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(activeColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "LV",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "LIVLINK",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "منصة ربط المحلات بالليفرورات",
                                        fontSize = 11.sp,
                                        color = Color(0xFFCBD5E1),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Refined Top Bar Icons (Equal 34dp size, uniform 6dp spacing, no crowding)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // 1. Store Icon (Shop Owner mode toggle)
                                IconButton(
                                    onClick = {
                                        onRoleChange("صاحب محل")
                                        Toast.makeText(context, "تم التبديل إلى وضع صاحب المحل (الأزرق)", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (!isDriver) ShopBluePrimary else NavySecondary)
                                        .border(
                                            width = 1.2.dp,
                                            color = if (!isDriver) Color.White.copy(alpha = 0.95f) else Color(0xFF334155),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = "وضع صاحب المحل",
                                        tint = if (!isDriver) Color.White else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(17.dp)
                                    )
                                }

                                // 2. Motorcycle with delivery box icon (Driver mode toggle)
                                IconButton(
                                    onClick = {
                                        onRoleChange("ليفرور")
                                        Toast.makeText(context, "تم التبديل إلى وضع سائق التوصيل (البرتقالي)", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (isDriver) DriverOrangePrimary else NavySecondary)
                                        .border(
                                            width = 1.2.dp,
                                            color = if (isDriver) Color.White.copy(alpha = 0.95f) else Color(0xFF334155),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                                        contentDescription = "وضع سائق التوصيل (ليفرور)",
                                        tint = if (isDriver) Color.White else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(17.dp)
                                    )
                                }

                                // 3. Dark/Light Mode Icon
                                IconButton(
                                    onClick = onToggleDarkMode,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NavySecondary)
                                        .border(
                                            width = 1.2.dp,
                                            color = Color(0xFF334155),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "تغيير الوضع الليلي / النهاري",
                                        tint = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFFE2E8F0),
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Corrected Official Banner (Exact phrase, without redundant tag)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = NavySecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = activeColor,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "منصة وطنية مغربية مجانية 100% تغطى جميع مدن المملكة",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFE2E8F0),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Role Indicator & City Selector Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Current Active Role Badge (Clickable to switch)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(activeColor.copy(alpha = 0.18f))
                                    .border(1.dp, activeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        val newRole = if (isDriver) "صاحب محل" else "ليفرور"
                                        onRoleChange(newRole)
                                        Toast.makeText(context, "الوضع النشط: $newRole", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isDriver) Icons.Default.DeliveryDining else Icons.Default.Store,
                                        contentDescription = null,
                                        tint = activeColor,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isDriver) "وضع الليفرور (نشط)" else "وضع صاحب المحل (نشط)",
                                        fontSize = 11.5.sp,
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
                                    .padding(horizontal = 8.dp, vertical = 7.dp)
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
                                            tint = activeColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = selectedCity,
                                            fontSize = 11.5.sp,
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
                                            text = { Text(city, fontSize = 12.sp) },
                                            onClick = {
                                                selectedCity = city
                                                cityDropdownExpanded = false
                                                Toast.makeText(context, "المدينة المحددة: $city", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    color = NavyPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                        containerColor = NavyPrimary,
                        tonalElevation = 8.dp
                    ) {
                        // Strictly 5 destinations: 1.الرئيسية, 2.العروض, 3.إضافة, 4.الليفرورات, 5.حسابي
                        val navItems = listOf(
                            Triple("الرئيسية", Icons.Default.Home, 0),
                            Triple("العروض", Icons.Default.ListAlt, 1),
                            Triple("إضافة", Icons.Default.AddCircle, 2),
                            Triple("الليفرورات", Icons.Default.DeliveryDining, 3),
                            Triple("حسابي", Icons.Default.Person, 4)
                        )

                        navItems.forEach { (label, icon, index) ->
                            val isSelected = currentTab == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentTab = index },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) activeColor else Color(0xFF94A3B8),
                                        modifier = Modifier.size(if (index == 2) 26.dp else 22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) activeColor else Color(0xFF94A3B8)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = activeColor,
                                    selectedTextColor = activeColor,
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8),
                                    indicatorColor = activeColor.copy(alpha = 0.18f)
                                )
                            )
                        }
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
                    // 1. الرئيسية
                    0 -> HomeTabContent(
                        isDriver = isDriver,
                        activeColor = activeColor,
                        selectedCity = selectedCity,
                        isDriverAvailable = isDriverAvailable,
                        onToggleDriverAvailability = {
                            isDriverAvailable = !isDriverAvailable
                            Toast.makeText(
                                context,
                                if (isDriverAvailable) "أنت الآن متاح لتلقي طلبيات التوصيل" else "تم إيقاف جاهزية التوصيل مؤقتاً",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        offers = offersList.filter {
                            selectedCity == "جميع مدن المغرب" || it.city == selectedCity
                        },
                        onNavigateToOffers = { currentTab = 1 },
                        onNavigateToAdd = { currentTab = 2 },
                        onNavigateToDrivers = { currentTab = 3 },
                        onSelectOffer = { selectedOfferForDetails = it },
                        onDial = { dialPhone(it) },
                        onWhatsApp = { openWhatsApp(it) }
                    )

                    // 2. العروض
                    1 -> OffersTabContent(
                        isDriver = isDriver,
                        activeColor = activeColor,
                        selectedCity = selectedCity,
                        offers = offersList.filter {
                            selectedCity == "جميع مدن المغرب" || it.city == selectedCity
                        },
                        onNavigateToAdd = { currentTab = 2 },
                        onSelectOffer = { selectedOfferForDetails = it },
                        onDial = { dialPhone(it) },
                        onWhatsApp = { openWhatsApp(it) }
                    )

                    // 3. إضافة (center tab)
                    2 -> AddScreen(
                        onDismiss = { currentTab = 0 },
                        initialType = if (isDriver) "عرض توصيل" else "طلب توصيل",
                        onAddSubmit = { title, city, phone, details, type ->
                            offersList.add(
                                0,
                                DeliveryOffer(
                                    id = System.currentTimeMillis().toString(),
                                    title = title,
                                    city = city,
                                    district = "وسط المدينة",
                                    roleType = type,
                                    phone = phone,
                                    timeAgo = "الآن",
                                    details = details,
                                    priceEstimate = "حسب الاتفاق"
                                )
                            )
                            Toast.makeText(context, "تم نشر إعلانك بنجاح في $city", Toast.LENGTH_SHORT).show()
                            currentTab = 1 // Switch to Offers to view it
                        }
                    )

                    // 4. الليفرورات
                    3 -> DriversTabContent(
                        isDriver = isDriver,
                        activeColor = activeColor,
                        selectedCity = selectedCity,
                        drivers = driversList.filter {
                            selectedCity == "جميع مدن المغرب" || it.city == selectedCity
                        },
                        onSelectDriver = { selectedDriverForDetails = it },
                        onDial = { dialPhone(it) },
                        onWhatsApp = { openWhatsApp(it) }
                    )

                    // 5. حسابي
                    4 -> ProfileTabContent(
                        isDriver = isDriver,
                        activeColor = activeColor,
                        selectedCity = selectedCity,
                        userName = profileName,
                        userPhone = profilePhone,
                        isDriverAvailable = isDriverAvailable,
                        onToggleDriverAvailability = {
                            isDriverAvailable = !isDriverAvailable
                            Toast.makeText(
                                context,
                                if (isDriverAvailable) "أنت متاح للتوصيل" else "حالتك متوقفة",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onSwitchRole = {
                            val nextRole = if (isDriver) "صاحب محل" else "ليفرور"
                            onRoleChange(nextRole)
                            Toast.makeText(context, "تم التحويل إلى: $nextRole", Toast.LENGTH_SHORT).show()
                        },
                        onOpenEditDialog = { showEditProfileDialog = true }
                    )
                }

                // Offer Details Dialog
                selectedOfferForDetails?.let { offer ->
                    AlertDialog(
                        onDismissRequest = { selectedOfferForDetails = null },
                        title = {
                            Text(
                                text = offer.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "نوع الإعلان: ${offer.roleType}",
                                    fontWeight = FontWeight.Bold,
                                    color = activeColor,
                                    fontSize = 13.sp
                                )
                                Text(text = "المدينة والحي: ${offer.city} • ${offer.district}", fontSize = 12.5.sp)
                                Text(text = "تفاصيل الطلب: ${offer.details}", fontSize = 12.sp, lineHeight = 17.sp)
                                Text(text = "رقم الهاتف: ${offer.phone}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    openWhatsApp(offer.phone)
                                    selectedOfferForDetails = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("واتساب")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    dialPhone(offer.phone)
                                    selectedOfferForDetails = null
                                }
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اتصال")
                            }
                        }
                    )
                }

                // Driver Details Dialog
                selectedDriverForDetails?.let { driver ->
                    AlertDialog(
                        onDismissRequest = { selectedDriverForDetails = null },
                        title = {
                            Text(
                                text = driver.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "المدينة: ${driver.city}", fontSize = 13.sp)
                                Text(text = "وسيلة النقل: ${driver.vehicle}", fontSize = 13.sp)
                                Text(text = "سنوات الخبرة: ${driver.experience}", fontSize = 13.sp)
                                Text(text = "التوصيلات المنجزة: ${driver.completedTrips} توصيلة", fontSize = 13.sp, color = activeColor, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (driver.isAvailable) "الحالة: متاح للتوصيل الفوري" else "الحالة: مشغول حالياً",
                                    fontWeight = FontWeight.Bold,
                                    color = if (driver.isAvailable) Color(0xFF16A34A) else Color.Gray,
                                    fontSize = 12.5.sp
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    dialPhone(driver.phone)
                                    selectedDriverForDetails = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اتصال مباشر")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    openWhatsApp(driver.phone)
                                    selectedDriverForDetails = null
                                }
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("واتساب")
                            }
                        }
                    )
                }

                // Edit Profile Dialog
                if (showEditProfileDialog) {
                    var tempName by remember { mutableStateOf(profileName) }
                    var tempPhone by remember { mutableStateOf(profilePhone) }

                    AlertDialog(
                        onDismissRequest = { showEditProfileDialog = false },
                        title = {
                            Text(
                                text = "تعديل بيانات الحساب",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = tempName,
                                    onValueChange = { tempName = it },
                                    label = { Text("الاسم المعتمد") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = tempPhone,
                                    onValueChange = { tempPhone = it },
                                    label = { Text("رقم الهاتف") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (tempName.isNotBlank() && tempPhone.isNotBlank()) {
                                        profileName = tempName
                                        profilePhone = tempPhone
                                        showEditProfileDialog = false
                                        Toast.makeText(context, "تم حفظ البيانات محلياً بنجاح", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                            ) {
                                Text("حفظ التعديل")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showEditProfileDialog = false }) {
                                Text("إلغاء")
                            }
                        }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 0: HOME CONTENT (Distinct for Shop Owner vs Delivery Driver)
// -------------------------------------------------------------
@Composable
fun HomeTabContent(
    isDriver: Boolean,
    activeColor: Color,
    selectedCity: String,
    isDriverAvailable: Boolean,
    onToggleDriverAvailability: () -> Unit,
    offers: List<DeliveryOffer>,
    onNavigateToOffers: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToDrivers: () -> Unit,
    onSelectOffer: (DeliveryOffer) -> Unit,
    onDial: (String) -> Unit,
    onWhatsApp: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card (Shop Owner vs Driver)
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = activeColor
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isDriver) "أهلاً بك يا كابتن التوصيل!" else "مرحباً بك يا صاحب المحل!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isDriver) "وضع الليفرور" else "وضع صاحب المحل",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isDriver)
                            "تصفح طلبيات المحلات والمطاعم بمدينة $selectedCity وتواصل فوراً مع أصحابها لبدء التوصيل وكسب دخل إضافي."
                        else
                            "اعثر على ليفرور موثوق لمحلك في $selectedCity وقم بإرسال طلبيات زبائنك بسرعة وأمان وبدون أي وسيط.",
                        fontSize = 12.5.sp,
                        color = Color(0xFFF8FAFC),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onNavigateToAdd,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDriver) "نشر عرض جاهزية للتوصيل" else "إضافة طلب توصيل جديد لمحلك",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Driver Availability Toggle Card (Only in Driver Mode)
        if (isDriver) {
            item {
                val isDark = MaterialTheme.colorScheme.background == BackgroundDark
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDriverAvailable) {
                            if (isDark) Color(0xFF064E3B).copy(alpha = 0.45f) else Color(0xFFECFDF5)
                        } else {
                            if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFF1F5F9)
                        }
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isDriverAvailable) {
                                if (isDark) Color(0xFF059669) else Color(0xFFA7F3D0)
                            } else {
                                if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                            }
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isDriverAvailable) Color(0xFF16A34A) else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isDriverAvailable) "حالتك: متاح حالياً للتوصيل" else "حالتك: غير متاح حالياً",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isDriverAvailable) {
                                        if (isDark) Color(0xFF6EE7B7) else Color(0xFF065F46)
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                Text(
                                    text = if (isDriverAvailable) "المحلات في $selectedCity يمكنها الاتصال بك الآن" else "إعلاناتك متوقفة مؤقتاً",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isDriverAvailable,
                            onCheckedChange = { onToggleDriverAvailability() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF16A34A)
                            )
                        )
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = "الوصول السريع والخدمات:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Action 1: Offers
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToOffers() },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                .background(activeColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = null,
                                tint = activeColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isDriver) "طلبيات المحلات" else "سوق العروض",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${offers.size} إعلان",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action 2: Add
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToAdd() },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                .background(activeColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null,
                                tint = activeColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isDriver) "نشر عرض" else "طلب ليفرور",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "نشر مجاني",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action 3: Drivers Directory
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDrivers() },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                imageVector = Icons.Default.DeliveryDining,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "الليفرورات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "دليل السائقين",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section Title: Latest Offers/Requests
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isDriver) "أحدث طلبات التوصيل المطلوبة ($selectedCity):" else "أحدث الإعلانات في ($selectedCity):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onNavigateToOffers) {
                    Text(
                        text = "عرض الكل",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = activeColor
                    )
                }
            }
        }

        if (offers.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد إعلانات حالياً في $selectedCity",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onNavigateToAdd) {
                            Text("أضف أول إعلان في هذه المدينة", color = activeColor, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(offers.take(3)) { offer ->
                OfferItemCard(
                    offer = offer,
                    activeColor = activeColor,
                    onClick = { onSelectOffer(offer) },
                    onDial = { onDial(offer.phone) },
                    onWhatsApp = { onWhatsApp(offer.phone) }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: OFFERS CONTENT
// -------------------------------------------------------------
@Composable
fun OffersTabContent(
    isDriver: Boolean,
    activeColor: Color,
    selectedCity: String,
    offers: List<DeliveryOffer>,
    onNavigateToAdd: () -> Unit,
    onSelectOffer: (DeliveryOffer) -> Unit,
    onDial: (String) -> Unit,
    onWhatsApp: (String) -> Unit
) {
    var filterType by remember { mutableStateOf("الكل") } // "الكل", "طلب توصيل", "عرض توصيل"

    val displayOffers = offers.filter {
        when (filterType) {
            "طلب توصيل" -> it.roleType == "طلب توصيل"
            "عرض توصيل" -> it.roleType == "عرض توصيل"
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
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
                        text = if (isDriver) "سوق طلبيات المحلات" else "سوق العروض والطلبيات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "المدينة: $selectedCity (${displayOffers.size} إعلان)",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onNavigateToAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = activeColor),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Filter chips row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("الكل", "طلب توصيل", "عرض توصيل").forEach { chip ->
                    val isSelected = filterType == chip
                    ElevatedFilterChip(
                        selected = isSelected,
                        onClick = { filterType = chip },
                        label = { Text(chip, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = activeColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        if (displayOffers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد إعلانات مطابقة في $selectedCity",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "يمكنك تغيير المدينة أو إضافة إعلان جديد الآن.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(displayOffers) { offer ->
                OfferItemCard(
                    offer = offer,
                    activeColor = activeColor,
                    onClick = { onSelectOffer(offer) },
                    onDial = { onDial(offer.phone) },
                    onWhatsApp = { onWhatsApp(offer.phone) }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: DRIVERS DIRECTORY (الليفرورات)
// -------------------------------------------------------------
@Composable
fun DriversTabContent(
    isDriver: Boolean,
    activeColor: Color,
    selectedCity: String,
    drivers: List<DriverProfile>,
    onSelectDriver: (DriverProfile) -> Unit,
    onDial: (String) -> Unit,
    onWhatsApp: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "دليل السائقين المعتمدين (الليفرورات)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "تواصل مباشرة مع الليفرور المتاح في $selectedCity بدون أي وسيط",
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
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(drivers) { driver ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectDriver(driver) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            val isDark = MaterialTheme.colorScheme.background == BackgroundDark
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (driver.isAvailable) {
                                            if (isDark) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFFDCFCE7)
                                        } else {
                                            if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeliveryDining,
                                    contentDescription = null,
                                    tint = if (driver.isAvailable) {
                                        if (isDark) Color(0xFF34D399) else Color(0xFF16A34A)
                                    } else {
                                        if (isDark) Color(0xFF94A3B8) else Color.Gray
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = driver.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (driver.isAvailable) Color(0xFFDCFCE7) else Color(0xFFE2E8F0))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (driver.isAvailable) "متاح" else "مشغول",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (driver.isAvailable) Color(0xFF16A34A) else Color.Gray
                                        )
                                    }
                                }
                                Text(
                                    text = "${driver.city} • ${driver.vehicle} (${driver.experience})",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${driver.completedTrips} توصيلة ناجحة",
                                    fontSize = 11.sp,
                                    color = activeColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Call & WhatsApp with generous touch targets (~48dp) and 8dp spacing
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // WhatsApp Button
                            IconButton(
                                onClick = { onWhatsApp(driver.phone) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "مراسلة واتساب",
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Dial Button
                            IconButton(
                                onClick = { onDial(driver.phone) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(NavyPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "اتصال هاتفي",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: MY ACCOUNT CONTENT (حسابي)
// -------------------------------------------------------------
@Composable
fun ProfileTabContent(
    isDriver: Boolean,
    activeColor: Color,
    selectedCity: String,
    userName: String,
    userPhone: String,
    isDriverAvailable: Boolean,
    onToggleDriverAvailability: () -> Unit,
    onSwitchRole: () -> Unit,
    onOpenEditDialog: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(activeColor.copy(alpha = 0.15f))
                            .border(2.dp, activeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDriver) Icons.Default.DeliveryDining else Icons.Default.Store,
                            contentDescription = null,
                            tint = activeColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = if (isDriver) "حساب سائق توصيل (ليفرور) • $selectedCity" else "حساب صاحب محل تجاري • $selectedCity",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Switch Role Button
                        OutlinedButton(
                            onClick = onSwitchRole,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = activeColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isDriver) "التبديل لوضع المحل" else "التبديل لوضع الليفرور",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Edit Profile Button
                        Button(
                            onClick = onOpenEditDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = activeColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تعديل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Account Details Section
        item {
            Text(
                text = "إعدادات ومعلومات الحساب:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الاسم المعتمد في المنصة:", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("رقم الهاتف للتواصل المباشر:", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(userPhone, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("المدينة المحددة حالياً:", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(selectedCity, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    if (isDriver) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("جاهزية التوصيل الحالية:", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Switch(
                                checked = isDriverAvailable,
                                onCheckedChange = { onToggleDriverAvailability() }
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("نموذج الاستخدام المعتمد:", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("مجاني 100٪ بدون عمولات", fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), fontSize = 13.sp)
                    }
                }
            }
        }

        // Technical Notice Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = activeColor,
                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "توضيح تقني: البيانات وتفضيلات الدور محفوظة محلياً في هذا الإصدار عبر SharedPreferences. الربط السحابي مع قاعدة البيانات الخارجية سيتم إدراجه في مرحلة مقبلة.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// REUSABLE OFFER CARD COMPONENT
// -------------------------------------------------------------
@Composable
fun OfferItemCard(
    offer: DeliveryOffer,
    activeColor: Color,
    onClick: () -> Unit,
    onDial: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                            .background(
                                if (offer.roleType == "طلب توصيل") ShopBluePrimary.copy(alpha = 0.12f)
                                else DriverOrangePrimary.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = offer.roleType,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = if (offer.roleType == "طلب توصيل") ShopBluePrimary else DriverOrangePrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${offer.city} • ${offer.district}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = offer.timeAgo,
                    fontSize = 10.5.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = offer.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = offer.details,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الهاتف: ${offer.phone}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Call and WhatsApp buttons with ample 8dp spacing and comfortable ~44dp height
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // WhatsApp Button
                    Button(
                        onClick = onWhatsApp,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "مراسلة واتساب",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("واتساب", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Dial Button
                    Button(
                        onClick = onDial,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "اتصال هاتفي",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("اتصال", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
