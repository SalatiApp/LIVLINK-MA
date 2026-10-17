package com.example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.DriverOrangePrimary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.ShopBluePrimary

val MOROCCAN_CITIES_LIST = listOf(
    "جميع مدن المغرب",
    "الدار البيضاء",
    "الرباط",
    "فاس",
    "طنجة",
    "مراكش",
    "أكادير",
    "مكناس",
    "وجدة",
    "القنيطرة",
    "تطوان",
    "تمارة",
    "آسفي",
    "المحمدية",
    "الناظور",
    "العيون",
    "بني ملال",
    "الجديدة",
    "تازة",
    "خريبكة"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onDismiss: () -> Unit = {},
    initialType: String = "طلب توصيل",
    initialCity: String = "الدار البيضاء",
    onAddSubmit: ((
        title: String,
        city: String,
        phone: String,
        details: String,
        type: String,
        latitude: Double?,
        longitude: Double?,
        locationAddress: String?
    ) -> Unit)? = null
) {
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var selectedType by remember(initialType) {
            mutableStateOf(
                if (initialType.contains("عرض") || initialType == "ليفرور") "عرض توصيل" else "طلب توصيل"
            )
        }
        val isDriverType = selectedType == "عرض توصيل"
        val activePrimary = if (isDriverType) DriverOrangePrimary else ShopBluePrimary

        var title by remember { mutableStateOf("") }
        var selectedCity by remember(initialCity) {
            mutableStateOf(if (initialCity == "جميع مدن المغرب" || initialCity.isBlank()) "الدار البيضاء" else initialCity)
        }
        var cityDropdownExpanded by remember { mutableStateOf(false) }
        var phone by remember { mutableStateOf("") }
        var details by remember { mutableStateOf("") }

        var shopLatitude by remember { mutableStateOf<Double?>(null) }
        var shopLongitude by remember { mutableStateOf<Double?>(null) }
        var shopAddress by remember { mutableStateOf("") }
        var showManualCoordDialog by remember { mutableStateOf(false) }
        var manualLatInput by remember { mutableStateOf("") }
        var manualLngInput by remember { mutableStateOf("") }

        var isSubmitted by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        fun detectCurrentLocation() {
            try {
                val finePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                val coarsePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

                if (finePerm == PackageManager.PERMISSION_GRANTED || coarsePerm == PackageManager.PERMISSION_GRANTED) {
                    val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                    val gpsLoc = lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val netLoc = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    val bestLoc: Location? = gpsLoc ?: netLoc

                    if (bestLoc != null) {
                        shopLatitude = (bestLoc.latitude * 100000.0).toLong() / 100000.0
                        shopLongitude = (bestLoc.longitude * 100000.0).toLong() / 100000.0
                        if (shopAddress.isBlank()) {
                            shopAddress = "موقع GPS محدد في $selectedCity"
                        }
                        Toast.makeText(context, "تم تحديد موقع المحل بنجاح: ${shopLatitude}, ${shopLongitude}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "تعذر التقاط إشارة GPS الحالية، يرجى إدخال الإحداثيات يدوياً", Toast.LENGTH_LONG).show()
                        showManualCoordDialog = true
                    }
                } else {
                    Toast.makeText(context, "يرجى منح إذن الموقع أولاً لتحديد مكان المحل عبر GPS", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "خطأ في تحديد الموقع: ${e.message}", Toast.LENGTH_SHORT).show()
                showManualCoordDialog = true
            }
        }

        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                detectCurrentLocation()
            } else {
                Toast.makeText(context, "تم رفض إذن الموقع، يمكنك إدخال إحداثيات المحل يدوياً", Toast.LENGTH_LONG).show()
                showManualCoordDialog = true
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            text = if (isDriverType) "إضافة عرض توصيل (ليفرور)" else "إضافة طلب توصيل جديد (محل)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NavyPrimary
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (isSubmitted) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .align(Alignment.Center),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "تم بنجاح",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isDriverType) "تم نشر عرض التوصيل بنجاح!" else "تم نشر طلب التوصيل بنجاح!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isDriverType)
                                    "عرضك أصبح متاحاً في قسم العروض بمدينة $selectedCity ويمكن لأصحاب المحلات الاتصال بك الآن."
                                else
                                    "طلبك منشور الآن في سوق العروض بمدينة $selectedCity وجاهز لاستقبال اتصالات الليفرورات.",
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = activePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("الانتقال إلى قائمة العروض", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "نوع الإعلان المراد نشره:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ElevatedCard(
                                onClick = { selectedType = "طلب توصيل" },
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (!isDriverType) ShopBluePrimary else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = null,
                                        tint = if (!isDriverType) Color.White else ShopBluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "طلب توصيل (محل)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isDriverType) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            ElevatedCard(
                                onClick = {
                                    selectedType = "عرض توصيل"
                                    shopLatitude = null
                                    shopLongitude = null
                                    shopAddress = ""
                                },
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (isDriverType) DriverOrangePrimary else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                                        contentDescription = null,
                                        tint = if (isDriverType) Color.White else DriverOrangePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "عرض توصيل (ليفرور)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDriverType) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = {
                                Text(if (!isDriverType) "اسم المحل ونوع السلع *" else "اسم الليفرور ووسيلة النقل *")
                            },
                            placeholder = {
                                Text(
                                    if (!isDriverType)
                                        "مثال: سناك السلام للمأكولات السريعة"
                                    else
                                        "مثال: كابتن مراد - دراجة نارية بصندوق حراري"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Column {
                            Text(
                                text = "المدينة التابعة للإعلان *",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                                    .clickable { cityDropdownExpanded = true }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = activePrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = selectedCity,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "اختيار المدينة",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                DropdownMenu(
                                    expanded = cityDropdownExpanded,
                                    onDismissRequest = { cityDropdownExpanded = false }
                                ) {
                                    MOROCCAN_CITIES_LIST.filter { it != "جميع مدن المغرب" }.forEach { city ->
                                        DropdownMenuItem(
                                            text = { Text(city, fontSize = 13.sp) },
                                            onClick = {
                                                selectedCity = city
                                                cityDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("رقم الهاتف أو الواتساب للتواصل المباشر *") },
                            placeholder = { Text("مثال: 0661234567") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = activePrimary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text("تفاصيل إضافية (الأحياء، شروط التوصيل، التوقيت)") },
                            placeholder = {
                                Text(
                                    if (!isDriverType)
                                        "مثال: مطلوب توصيل طلبيات يومية في حي المعاريف، أوقات العمل من 12 إلى 20..."
                                    else
                                        "مثال: جاهز للتوصيل الفوري بمدينة $selectedCity وضواحيها، التزام وسرعة..."
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(10.dp)
                        )

                        if (!isDriverType) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NavySecondary.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, ShopBluePrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = ShopBluePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "موقع المحل على الخريطة (اختياري لصاحب المحل)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                    }

                                    Text(
                                        text = "يسمح بتحديد إحداثيات موقع محلك الجغرافي لتسهيل وصول الليفرورات إليك عبر الخريطة.",
                                        fontSize = 12.sp,
                                        color = Color(0xFFCBD5E1),
                                        lineHeight = 17.sp
                                    )

                                    if (shopLatitude != null && shopLongitude != null) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF064E3B).copy(alpha = 0.5f))
                                                .border(1.dp, Color(0xFF16A34A), RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "تم حفظ موقع المحل بنجاح",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF4ADE80)
                                                    )
                                                    Text(
                                                        text = "الإحداثيات: $shopLatitude, $shopLongitude",
                                                        fontSize = 11.sp,
                                                        color = Color.White
                                                    )
                                                    if (shopAddress.isNotBlank()) {
                                                        Text(
                                                            text = "العنوان: $shopAddress",
                                                            fontSize = 11.sp,
                                                            color = Color(0xFFE2E8F0)
                                                        )
                                                    }
                                                }
                                                IconButton(onClick = {
                                                    shopLatitude = null
                                                    shopLongitude = null
                                                    shopAddress = ""
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "حذف الموقع",
                                                        tint = Color(0xFFF87171),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "الموقع الحالي: غير محدد (يمكنك المتابعة دونه)",
                                            fontSize = 12.sp,
                                            color = Color(0xFF94A3B8)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    val finePerm = ContextCompat.checkSelfPermission(
                                                        context,
                                                        Manifest.permission.ACCESS_FINE_LOCATION
                                                    )
                                                    if (finePerm == PackageManager.PERMISSION_GRANTED) {
                                                        detectCurrentLocation()
                                                    } else {
                                                        locationPermissionLauncher.launch(
                                                            arrayOf(
                                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                                            )
                                                        )
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ShopBluePrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MyLocation,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("تحديد عبر GPS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = { showManualCoordDialog = true },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.EditLocation,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("إدخال يدوي", fontSize = 12.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                val cleanTitle = title.trim()
                                val cleanPhone = phone.trim().replace(" ", "").replace("-", "")

                                if (cleanTitle.length < 3) {
                                    errorMessage = "يرجى كتابة اسم صحيح (3 أحرف على الأقل)"
                                } else if (cleanPhone.length < 8) {
                                    errorMessage = "يرجى كتابة رقم هاتف صحيح للتواصل"
                                } else {
                                    errorMessage = null
                                    onAddSubmit?.invoke(
                                        cleanTitle,
                                        selectedCity,
                                        cleanPhone,
                                        details.trim(),
                                        selectedType,
                                        if (!isDriverType) shopLatitude else null,
                                        if (!isDriverType) shopLongitude else null,
                                        if (!isDriverType && shopAddress.isNotBlank()) shopAddress.trim() else null
                                    )
                                    isSubmitted = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = activePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = if (!isDriverType) "نشر طلب التوصيل (محل)" else "نشر عرض التوصيل (ليفرور)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                if (showManualCoordDialog) {
                    AlertDialog(
                        onDismissRequest = { showManualCoordDialog = false },
                        title = {
                            Text(
                                text = "تحديد إحداثيات موقع المحل يدوياً",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "أدخل خط العرض (Latitude) وخط الطول (Longitude) أو عنوان المحل:",
                                    fontSize = 12.sp
                                )
                                OutlinedTextField(
                                    value = manualLatInput,
                                    onValueChange = { manualLatInput = it },
                                    label = { Text("خط العرض (مثال: 33.5731)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = manualLngInput,
                                    onValueChange = { manualLngInput = it },
                                    label = { Text("خط الطول (مثال: -7.5898)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = shopAddress,
                                    onValueChange = { shopAddress = it },
                                    label = { Text("عنوان أو حي المحل") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val lat = manualLatInput.toDoubleOrNull()
                                    val lng = manualLngInput.toDoubleOrNull()
                                    if (lat != null && lng != null) {
                                        shopLatitude = lat
                                        shopLongitude = lng
                                        showManualCoordDialog = false
                                        Toast.makeText(context, "تم حفظ الإحداثيات بنجاح", Toast.LENGTH_SHORT).show()
                                    } else if (shopAddress.isNotBlank()) {
                                        shopLatitude = 33.5731
                                        shopLongitude = -7.5898
                                        showManualCoordDialog = false
                                        Toast.makeText(context, "تم اعتماد موقع المحل", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "يرجى إدخال إحداثيات صحيحة", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ShopBluePrimary)
                            ) {
                                Text("تعديل وحفظ الموقع")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showManualCoordDialog = false }) {
                                Text("إلغاء")
                            }
                        }
                    )
                }
            }
        }
    }
}
