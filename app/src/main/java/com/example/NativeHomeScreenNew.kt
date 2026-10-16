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
    val priceEstimate: String = "حسب الاتفاق",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationAddress: String? = null
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

// Persistence Helpers for SharedPreferences
fun saveOffersToPrefs(context: Context, offers: List<DeliveryOffer>) {
    try {
        val prefs = context.getSharedPreferences("livlink_prefs", Context.MODE_PRIVATE)
        val jsonArr = org.json.JSONArray()
        for (offer in offers) {
            val obj = org.json.JSONObject()
            obj.put("id", offer.id)
            obj.put("title", offer.title)
            obj.put("city", offer.city)
            obj.put("district", offer.district)
            obj.put("roleType", offer.roleType)
            obj.put("phone", offer.phone)
            obj.put("timeAgo", offer.timeAgo)
            obj.put("details", offer.details)
            obj.put("vehicleType", offer.vehicleType)
            obj.put("priceEstimate", offer.priceEstimate)
            if (offer.latitude != null) obj.put("latitude", offer.latitude)
            if (offer.longitude != null) obj.put("longitude", offer.longitude)
            if (offer.locationAddress != null) obj.put("locationAddress", offer.locationAddress)
            jsonArr.put(obj)
        }
        prefs.edit().putString("saved_offers_json", jsonArr.toString()).apply()
    } catch (e: Exception) {
        // Silently preserve stability
    }
}

fun loadOffersFromPrefs(context: Context): List<DeliveryOffer>? {
    return try {
        val prefs = context.getSharedPreferences("livlink_prefs", Context.MODE_PRIVATE)
        val jsonStr = prefs.getString("saved_offers_json", null) ?: return null
        val jsonArr = org.json.JSONArray(jsonStr)
        val list = mutableListOf<DeliveryOffer>()
        for (i in 0 until jsonArr.length()) {
            val obj = jsonArr.getJSONObject(i)
            list.add(
                DeliveryOffer(
                    id = obj.optString("id", System.currentTimeMillis().toString()),
                    title = obj.optString("title", ""),
                    city = obj.optString("city", "الدار البيضاء"),
                    district = obj.optString("district", "وسط المدينة"),
                    roleType = obj.optString("roleType", "طلب توصيل"),
                    phone = obj.optString("phone", ""),
                    timeAgo = obj.optString("timeAgo", "مؤخراً"),
                    details = obj.optString("details", ""),
                    vehicleType = obj.optString("vehicleType", "دراجة نارية"),
                    priceEstimate = obj.optString("priceEstimate", "حسب الاتفاق"),
                    latitude = if (obj.has("latitude") && !obj.isNull("latitude")) obj.optDouble("latitude") else null,
                    longitude = if (obj.has("longitude") && !obj.isNull("longitude")) obj.optDouble("longitude") else null,
                    locationAddress = if (obj.has("locationAddress") && !obj.isNull("locationAddress")) obj.optString("locationAddress") else null
                )
            )
        }
        if (list.isNotEmpty()) list else null
    } catch (e: Exception) {
        null
    }
}

fun saveDriversToPrefs(context: Context, drivers: List<DriverProfile>) {
    try {
        val prefs = context.getSharedPreferences("livlink_prefs", Context.MODE_PRIVATE)
        val jsonArr = org.json.JSONArray()
        for (driver in drivers) {
            val obj = org.json.JSONObject()
            obj.put("id", driver.id)
            obj.put("name", driver.name)
            obj.put("city", driver.city)
            obj.put("vehicle", driver.vehicle)
            obj.put("phone", driver.phone)
            obj.put("isAvailable", driver.isAvailable)
            obj.put("experience", driver.experience)
            obj.put("completedTrips", driver.completedTrips)
            jsonArr.put(obj)
        }
        prefs.edit().putString("saved_drivers_json", jsonArr.toString()).apply()
    } catch (e: Exception) {
        // Silently preserve stability
    }
}

fun loadDriversFromPrefs(context: Context): List<DriverProfile>? {
    return try {
        val prefs = context.getSharedPreferences("livlink_prefs", Context.MODE_PRIVATE)
        val jsonStr = prefs.getString("saved_drivers_json", null) ?: return null
        val jsonArr = org.json.JSONArray(jsonStr)
        val list = mutableListOf<DriverProfile>()
        for (i in 0 until jsonArr.length()) {
            val obj = jsonArr.getJSONObject(i)
            list.add(
                DriverProfile(
                    id = obj.optString("id", System.currentTimeMillis().toString()),
                    name = obj.optString("name", ""),
                    city = obj.optString("city", "الدار البيضاء"),
                    vehicle = obj.optString("vehicle", "دراجة نارية مع صندوق"),
                    phone = obj.optString("phone", ""),
                    isAvailable = obj.optBoolean("isAvailable", true),
                    experience = obj.optString("experience", "سائق متاح للتوصيل"),
                    completedTrips = obj.optInt("completedTrips", 0)
                )
            )
        }
        if (list.isNotEmpty()) list else null
    } catch (e: Exception) {
        null
    }
}

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

        // Persistent Offers list
        val defaultInitialOffers = remember {
            listOf(
                DeliveryOffer(
                    id = "1",
                    title = "محل إلكترونيات وأكسسوارات الهواتف",
                    city = "الدار البيضاء",
                    district = "المعاريف / سيدي معروف",
                    roleType = "طلب توصيل",
                    phone = "0661001122",
                    timeAgo = "منذ 10 دقائق",
                    details = "مطلوب ليفرور لتوصيل طلبيات هواتف وأكسسوارات داخل المعاريف وسيدي معروف يومياً.",
                    priceEstimate = "25 درهم / طلبية",
                    latitude = 33.5898,
                    longitude = -7.6324,
                    locationAddress = "شارع المسيرة الخضراء، المعاريف"
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
                    priceEstimate = "20 درهم / توصيلة",
                    latitude = 34.0333,
                    longitude = -5.0000,
                    locationAddress = "شارع الحسن الثاني، فاس الجديد"
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
                    priceEstimate = "30 درهم / إرسالية",
                    latitude = 31.6341,
                    longitude = -8.0125,
                    locationAddress = "جيليز، شارع محمد الخامس"
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
                    priceEstimate = "25 درهم / طلبية",
                    latitude = 30.4278,
                    longitude = -9.5981,
                    locationAddress = "حي السلام، أكادير"
                )
            )
        }

        val offersList = remember {
            val saved = loadOffersFromPrefs(context)
            mutableStateListOf<DeliveryOffer>().apply {
                addAll(saved ?: defaultInitialOffers)
            }
        }

        val defaultInitialDrivers = remember {
            listOf(
                DriverProfile("d1", "يوسف العمراني", "الدار البيضاء", "دراجة نارية مع صندوق", "0661122334", true, "3 سنوات", 420),
                DriverProfile("d2", "كريم السبتي", "الرباط", "دراجة نارية مع صندوق", "0672233445", true, "سنتان", 280),
                DriverProfile("d3", "حمزة التازي", "فاس", "دراجة نارية", "0653344556", true, "4 سنوات", 510),
                DriverProfile("d4", "أنس البورقادي", "طنجة", "سيارة خفيفة", "0664455667", false, "5 سنوات", 630),
                DriverProfile("d5", "رشيد المرابط", "مراكش", "دراجة نارية مع صندوق", "0675566778", true, "سنة واحدة", 150),
                DriverProfile("d6", "طارق بنعلي", "أكادير", "دراجة نارية مع صندوق", "0656677889", true, "سنتان", 310)
            )
        }

        val driversList = remember {
            val saved = loadDriversFromPrefs(context)
            mutableStateListOf<DriverProfile>().apply {
                addAll(saved ?: defaultInitialDrivers)
            }
        }

        // Helper call functions with full error handling and clean normalization
        fun dialPhone(phoneNumber: String) {
            val clean = phoneNumber.filter { it.isDigit() || it == '+' }
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
            val digits = phoneNumber.filter { it.isDigit() }
            if (digits.isBlank()) {
                Toast.makeText(context, "رقم الهاتف غير متوفر", Toast.LENGTH_SHORT).show()
                return
            }
            val intPhone = when {
                digits.startsWith("212") -> digits
                digits.startsWith("0") -> "212" + digits.substring(1)
                else -> "212$digits"
            }
            val uri = Uri.parse("https://wa.me/$intPhone")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                dialPhone(phoneNumber)
            }
        }

        // Open Map for shop requests with coordinates or address
        fun openMap(latitude: Double?, longitude: Double?, title: String, address: String? = null) {
            val uri = if (latitude != null && longitude != null) {
                Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(title)})")
            } else if (!address.isNullOrBlank()) {
                Uri.parse("geo:0,0?q=${Uri.encode(address)}")
            } else {
                Toast.makeText(context, "موقع المحل غير متوفر على الخريطة", Toast.LENGTH_SHORT).show()
                return
            }

            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            try {
                context.startActivity(mapIntent)
            } catch (e: Exception) {
                // Fallback to Google Maps Web
                val webUri = if (latitude != null && longitude != null) {
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                } else {
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(address ?: "")}")
                }
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                try {
                    context.startActivity(webIntent)
                } catch (e2: Exception) {
                    Toast.makeText(context, "موقع المحل: ${address ?: "$latitude, $longitude"}", Toast.LENGTH_LONG).show()
                }
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
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // 1. Header row: Brand, Slogan, and Reorganized Top Bar Icons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Brand Logo and Title
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(activeColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "LV",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(9.dp))
                                Column {
                                    Text(
                                        text = "LIVLINK",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "منصة ربط المحلات بالليفرورات",
                                        fontSize = 12.5.sp,
                                        color = Color(0xFFE2E8F0),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Three completely independent circular buttons
                            // Guaranteed 32dp size, 8dp equal spacing, zero overlapping bounds
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.wrapContentSize()
                            ) {
                                // 1. Store Icon (Shop Owner mode toggle)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (!isDriver) ShopBluePrimary else Color(0xFF1E293B))
                                        .border(
                                            width = 1.dp,
                                            color = if (!isDriver) Color(0xFF93C5FD) else Color(0xFF334155),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onRoleChange("صاحب محل")
                                            Toast.makeText(context, "تم التبديل إلى وضع صاحب المحل (الأزرق الملكي)", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = "وضع صاحب المحل",
                                        tint = if (!isDriver) Color.White else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 2. Motorcycle with delivery cargo box icon (Driver mode toggle)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isDriver) DriverOrangePrimary else Color(0xFF1E293B))
                                        .border(
                                            width = 1.dp,
                                            color = if (isDriver) Color(0xFFFED7AA) else Color(0xFF334155),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onRoleChange("ليفرور")
                                            Toast.makeText(context, "تم التبديل إلى وضع سائق التوصيل (البرتقالي)", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                                        contentDescription = "وضع سائق التوصيل (ليفرور)",
                                        tint = if (isDriver) Color.White else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 3. Dark/Light Mode Icon
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B))
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF334155),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onToggleDarkMode()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "تغيير الوضع الليلي / النهاري",
                                        tint = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(11.dp))

                        // 2. Official National Banner (Balanced spacing and contrast)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = NavySecondary
                            ),
                            shape = RoundedCornerShape(9.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = activeColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "منصة وطنية مغربية مجانية 100% تغطى جميع مدن المملكة",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFFF1F5F9),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(11.dp))

                        // 3. Role Indicator & City Selector Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Current Active Role Badge (Clickable to switch)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(activeColor.copy(alpha = 0.18f))
                                    .border(1.dp, activeColor.copy(alpha = 0.5f), RoundedCornerShape(9.dp))
                                    .clickable {
                                        val newRole = if (isDriver) "صاحب محل" else "ليفرور"
                                        onRoleChange(newRole)
                                        Toast.makeText(context, "الوضع النشط: $newRole", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isDriver) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                                            contentDescription = null,
                                            tint = activeColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Store,
                                            contentDescription = null,
                                            tint = activeColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isDriver) "وضع الليفرور (نشط)" else "وضع صاحب المحل (نشط)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // City Selector Dropdown
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(NavySecondary)
                                    .clickable { cityDropdownExpanded = true }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
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
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = selectedCity,
                                            fontSize = 12.5.sp,
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
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = cityDropdownExpanded,
                                    onDismissRequest = { cityDropdownExpanded = false }
                                ) {
                                    MOROCCAN_CITIES.forEach { city ->
                                        DropdownMenuItem(
                                            text = { Text(city, fontSize = 13.sp) },
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
                                    if (index == 3) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                                            contentDescription = label,
                                            tint = if (isSelected) activeColor else Color(0xFFCBD5E1),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = if (isSelected) activeColor else Color(0xFFCBD5E1),
                                            modifier = Modifier.size(if (index == 2) 26.dp else 22.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) activeColor else Color(0xFFCBD5E1)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = activeColor,
                                    selectedTextColor = activeColor,
                                    unselectedIconColor = Color(0xFFCBD5E1),
                                    unselectedTextColor = Color(0xFFCBD5E1),
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
                        onWhatsApp = { openWhatsApp(it) },
                        onOpenMap = { lat, lng, lbl, addr -> openMap(lat, lng, lbl, addr) }
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
                        onWhatsApp = { openWhatsApp(it) },
                        onOpenMap = { lat, lng, lbl, addr -> openMap(lat, lng, lbl, addr) }
                    )

                    // 3. إضافة (center tab)
                    2 -> AddScreen(
                        onDismiss = { currentTab = 1 },
                        initialType = if (isDriver) "عرض توصيل" else "طلب توصيل",
                        initialCity = selectedCity,
                        onAddSubmit = { title, city, phone, details, type, lat, lng, address ->
                            val cleanVehicle = if (type == "عرض توصيل") {
                                if (title.contains("-")) title.substringAfter("-").trim() else "دراجة نارية مع صندوق"
                            } else {
                                "طلب توصيل"
                            }
                            val cleanName = if (type == "عرض توصيل" && title.contains("-")) {
                                title.substringBefore("-").trim()
                            } else {
                                title
                            }

                            val newOffer = DeliveryOffer(
                                id = System.currentTimeMillis().toString(),
                                title = title,
                                city = city,
                                district = "وسط المدينة",
                                roleType = type,
                                phone = phone,
                                timeAgo = "الآن",
                                details = if (details.isBlank()) (if (type == "عرض توصيل") "سائق دراجة نارية جاهز للتوصيل الفوري بمدينة $city" else "طلب توصيل طلبيات بمدينة $city") else details,
                                vehicleType = cleanVehicle,
                                priceEstimate = "حسب الاتفاق",
                                latitude = lat,
                                longitude = lng,
                                locationAddress = address
                            )
                            offersList.add(0, newOffer)
                            saveOffersToPrefs(context, offersList.toList())

                            // If Driver offer, also add to drivers directory and persist
                            if (type == "عرض توصيل") {
                                val newDriver = DriverProfile(
                                    id = "d_${System.currentTimeMillis()}",
                                    name = cleanName,
                                    city = city,
                                    vehicle = cleanVehicle,
                                    phone = phone,
                                    isAvailable = true,
                                    experience = "سائق متاح للتوصيل",
                                    completedTrips = 1
                                )
                                driversList.add(0, newDriver)
                                saveDriversToPrefs(context, driversList.toList())
                            }

                            // Ensure newly published city is selected so offer is immediately visible
                            if (selectedCity != "جميع مدن المغرب" && selectedCity != city) {
                                selectedCity = city
                            }

                            Toast.makeText(context, "تم نشر إعلانك بنجاح في $city", Toast.LENGTH_SHORT).show()
                            currentTab = 1 // Switch to Offers tab immediately
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
                                Text(text = "تفاصيل الإعلان: ${offer.details}", fontSize = 12.sp, lineHeight = 17.sp)
                                Text(text = "رقم الهاتف: ${offer.phone}", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                // Driver Offer Info
                                if (offer.roleType == "عرض توصيل") {
                                    if (offer.vehicleType.isNotBlank()) {
                                        Text(
                                            text = "وسيلة النقل: ${offer.vehicleType}",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DriverOrangePrimary
                                        )
                                    }
                                    if (offer.priceEstimate.isNotBlank()) {
                                        Text(
                                            text = "التسعيرة: ${offer.priceEstimate}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Shop Location info (only if shop request and set)
                                if (offer.roleType == "طلب توصيل") {
                                    if (offer.latitude != null && offer.longitude != null) {
                                        Text(
                                            text = "موقع المحل: ${offer.locationAddress ?: "إحداثيات محددة (${offer.latitude}, ${offer.longitude})"}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ShopBluePrimary
                                        )
                                    } else if (!offer.locationAddress.isNullOrBlank()) {
                                        Text(
                                            text = "عنوان المحل: ${offer.locationAddress}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ShopBluePrimary
                                        )
                                    } else {
                                        Text(
                                            text = "موقع المحل: غير محدد",
                                            fontSize = 11.5.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        openWhatsApp(offer.phone)
                                        selectedOfferForDetails = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "واتساب",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("واتساب")
                                }

                                if (offer.roleType == "طلب توصيل" && ((offer.latitude != null && offer.longitude != null) || !offer.locationAddress.isNullOrBlank())) {
                                    Button(
                                        onClick = {
                                            openMap(offer.latitude, offer.longitude, offer.title, offer.locationAddress)
                                            selectedOfferForDetails = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ShopBluePrimary)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "موقع المحل",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("الخريطة")
                                    }
                                }
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    dialPhone(offer.phone)
                                    selectedOfferForDetails = null
                                }
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "اتصال", modifier = Modifier.size(16.dp))
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
                                    openWhatsApp(driver.phone)
                                    selectedDriverForDetails = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_whatsapp),
                                    contentDescription = "واتساب",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("واتساب")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    dialPhone(driver.phone)
                                    selectedDriverForDetails = null
                                }
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "اتصال", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اتصال مباشر")
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
    onWhatsApp: (String) -> Unit,
    onOpenMap: (Double?, Double?, String, String?) -> Unit
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
                                    fontSize = 12.5.sp,
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
                fontSize = 14.5.sp,
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
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${offers.size} إعلان",
                            fontSize = 12.5.sp,
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
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "نشر مجاني",
                            fontSize = 12.5.sp,
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
                        val isDark = MaterialTheme.colorScheme.background == BackgroundDark
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF064E3B).copy(alpha = 0.45f) else Color(0xFFF0FDF4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF34D399) else Color(0xFF16A34A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "الليفرورات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "دليل السائقين",
                            fontSize = 12.5.sp,
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
                    onWhatsApp = { onWhatsApp(offer.phone) },
                    onOpenMap = onOpenMap
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
    onWhatsApp: (String) -> Unit,
    onOpenMap: (Double?, Double?, String, String?) -> Unit
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
                        text = "سوق العروض والطلبيات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isDriver) "المدينة: $selectedCity (${displayOffers.size} طلبيات وعروض متاحة)"
                               else "المدينة: $selectedCity (${displayOffers.size} طلب وعرض معتمد)",
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
                    onWhatsApp = { onWhatsApp(offer.phone) },
                    onOpenMap = onOpenMap
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
                                        fontSize = 14.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (driver.isAvailable) Color(0xFFDCFCE7) else Color(0xFFE2E8F0))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (driver.isAvailable) "متاح" else "مشغول",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (driver.isAvailable) Color(0xFF16A34A) else Color.Gray
                                        )
                                    }
                                }
                                Text(
                                    text = "${driver.city} • ${driver.vehicle} (${driver.experience})",
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${driver.completedTrips} توصيلة ناجحة",
                                    fontSize = 12.sp,
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
    onWhatsApp: () -> Unit,
    onOpenMap: ((Double?, Double?, String, String?) -> Unit)? = null
) {
    val isDriverOffer = offer.roleType == "عرض توصيل"

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
            // Header Row: Role Badge + City/District + Time
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
                                if (!isDriverOffer) ShopBluePrimary.copy(alpha = 0.12f)
                                else DriverOrangePrimary.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isDriverOffer) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                                    contentDescription = null,
                                    tint = DriverOrangePrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Store,
                                    contentDescription = null,
                                    tint = ShopBluePrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isDriverOffer) "عرض توصيل (ليفرور)" else "طلب توصيل (محل)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = if (!isDriverOffer) ShopBluePrimary else DriverOrangePrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${offer.city} • ${offer.district}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = offer.timeAgo,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = offer.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Distinctive Vehicle Badge for Driver Offer
            if (isDriverOffer && offer.vehicleType.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DriverOrangePrimary.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                        contentDescription = null,
                        tint = DriverOrangePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "وسيلة النقل: ${offer.vehicleType}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DriverOrangePrimary
                    )
                    if (offer.priceEstimate.isNotBlank() && offer.priceEstimate != "حسب الاتفاق") {
                        Text(
                            text = " • ${offer.priceEstimate}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Location address for Shop Request
            if (!isDriverOffer && !offer.locationAddress.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = ShopBluePrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = offer.locationAddress ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = offer.details,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Actions row: Phone & Functional Buttons (WhatsApp, Dial, Location)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الهاتف: ${offer.phone}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Buttons row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // WhatsApp Button
                    Button(
                        onClick = onWhatsApp,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "مراسلة واتساب",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("واتساب", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Dial Button
                    Button(
                        onClick = onDial,
                        colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "اتصال هاتفي",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("اتصال", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Location Button (for Shop orders with coordinates or address)
                    if (!isDriverOffer && ((offer.latitude != null && offer.longitude != null) || !offer.locationAddress.isNullOrBlank()) && onOpenMap != null) {
                        Button(
                            onClick = { onOpenMap(offer.latitude, offer.longitude, offer.title, offer.locationAddress) },
                            colors = ButtonDefaults.buttonColors(containerColor = ShopBluePrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "موقع المحل",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("الموقع", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
