package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NavyPrimary

val MOROCCAN_CITIES = listOf(
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
    onAddSubmit: ((title: String, city: String, phone: String, details: String, type: String) -> Unit)? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val activePrimary = MaterialTheme.colorScheme.primary
        var selectedType by remember { mutableStateOf(if (initialType.contains("عرض") || initialType == "ليفرور") "عرض توصيل" else "طلب توصيل") }
        var title by remember { mutableStateOf("") }
        var selectedCity by remember { mutableStateOf("الدار البيضاء") }
        var cityDropdownExpanded by remember { mutableStateOf(false) }
        var phone by remember { mutableStateOf("") }
        var details by remember { mutableStateOf("") }
        var isSubmitted by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            text = if (selectedType == "طلب توصيل") "إضافة طلب توصيل جديد" else "إضافة عرض ليفرور جديد",
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
                                text = "تم نشر الإعلان بنجاح في منصة LIVLINK",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "إعلانك ظاهر الآن لجميع مستخدمي المنصة في مدينة $selectedCity بنجاح.",
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = activePrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("العودة إلى الرئيسية", fontWeight = FontWeight.Bold, color = Color.White)
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
                        // Type Selector (Shop vs Driver)
                        Text(
                            text = "نوع الإعلان المراد نشره:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
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
                                    containerColor = if (selectedType == "طلب توصيل") activePrimary else MaterialTheme.colorScheme.surface
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
                                        tint = if (selectedType == "طلب توصيل") Color.White else NavyPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "طلب توصيل (محل)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedType == "طلب توصيل") Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            ElevatedCard(
                                onClick = { selectedType = "عرض توصيل" },
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (selectedType == "عرض توصيل") activePrimary else MaterialTheme.colorScheme.surface
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
                                        imageVector = Icons.Default.DeliveryDining,
                                        contentDescription = null,
                                        tint = if (selectedType == "عرض توصيل") Color.White else activePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "عرض توصيل (ليفرور)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedType == "عرض توصيل") Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Title field
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = {
                                Text(if (selectedType == "طلب توصيل") "اسم المحل ونوع السلع *" else "اسم الليفرور ووسيلة النقل *")
                            },
                            placeholder = {
                                Text(if (selectedType == "طلب توصيل") "مثال: مأكولات سريعة / مستلزمات طبية" else "مثال: أمين - دراجة نارية للتوصيل السريع")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        // City selector dropdown
                        Column {
                            Text(
                                text = "المدينة (تغطية وطنية شاملة للمغرب) *",
                                fontSize = 12.sp,
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
                                    MOROCCAN_CITIES.filter { it != "جميع مدن المغرب" }.forEach { city ->
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

                        // Phone field
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("رقم الهاتف أو الواتساب للتواصل المباشر *") },
                            placeholder = { Text("06XXXXXXXX") },
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

                        // Details field
                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text("تفاصيل إضافية (الحي، توقيت العمل، شروط التوصيل)") },
                            placeholder = { Text("أدخل الأحياء المستهدفة، أوقات العمل المفضلة، أو أي تفاصيل مهمة...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape = RoundedCornerShape(10.dp)
                        )

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (title.isBlank() || phone.isBlank()) {
                                    errorMessage = "يرجى ملء الحقول الإلزامية (الاسم ورقم الهاتف)"
                                } else {
                                    errorMessage = null
                                    onAddSubmit?.invoke(title, selectedCity, phone, details, selectedType)
                                    isSubmitted = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = activePrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = if (selectedType == "طلب توصيل") "نشر طلب التوصيل" else "نشر عرض التوصيل",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
