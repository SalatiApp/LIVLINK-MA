package com.example

import android.content.Context
import android.content.Intent
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

val ShopBlueDarkPrimary = Color(0xFF1E40AF)
val ShopBlueAccent = Color(0xFF2563EB)

val MOROCCAN_CITIES = listOf(
    "جميع مدن المغرب",
    "الدار البيضاء",
    "الرباط",
    "فاس",
    "مراكش",
    "طنجة",
    "أكادير",
    "سلا",
    "مكناس",
    "وجدة",
    "القنيطرة",
    "تطوان",
    "أسفي",
    "محمدية",
    "خريبكة",
    "الجديدة",
    "بني ملال",
    "تمارة",
    "الصخيرات",
    "الناظور",
    "العرائش",
    "الحسيمة",
    "تازة",
    "سطات",
    "برشيد",
    "الخميسات",
    "القصر الكبير",
    "الصويرة",
    "ورزازات",
    "زاكورة",
    "الرشيدية",
    "ميدلت",
    "إفران",
    "آزرو",
    "صفرو",
    "تاونات",
    "شفشاون",
    "وزان",
    "أصيلة",
    "المضيق",
    "الفنيدق",
    "مرتيل",
    "إمزورن",
    "بني بوعياش",
    "العيون",
    "الداخلة",
    "كلميم",
    "طانطان",
    "سيدي إفني",
    "السمارة",
    "بوجدور",
    "تزنيت",
    "تارودانت",
    "إنزكان",
    "آيت ملول",
    "الدشيرة الجهادية",
    "بيوكرى",
    "بركان",
    "تاوريرت",
    "جرادة",
    "بوعرفة",
    "فجيج",
    "العروي",
    "زايو",
    "سلوان",
    "بني أنصار",
    "الدريوش",
    "سيدي قاسم",
    "سيدي سليمان",
    "سيدي يحيى الغرب",
    "أزمور",
    "سيدي بنور",
    "اليوسفية",
    "شيشاوة",
    "قلعة السراغنة",
    "ابن جرير",
    "الفقيه بن صالح",
    "أزيلال",
    "سوق السبت",
    "وادي زم",
    "أبي الجعد",
    "خنيفرة",
    "بنسليمان",
    "الحاجب",
    "بوفكران",
    "مولاي ادريس زرهون",
    "تنغير",
    "الجبهة",
    "مولاي يعقوب"
)

val CITY_DISTRICTS_MAP = mapOf(
    "جميع مدن المغرب" to listOf("اختر الحي", "جميع الأحياء"),
    "الدار البيضاء" to listOf("اختر الحي", "جميع الأحياء", "المعاريف", "أنفا", "سيدي معروف", "عين الشق", "سيدي مومن", "المحمدية / البرنوصي", "عين السبع", "الحي الحسني", "الحي المحمدي", "درب السلطان", "بوركون", "غوتييه", "كاليفورنيا", "سباتة", "مولاي رشيد", "بوسكورة", "عين الذئاب", "الوازيس", "بلفدير", "ليساسفة", "بن مسيك", "الفداء", "سيدي عثمان", "تيط مليل", "الهراويين", "النخيل", "الألفة", "لاجيروند", "الأحباس", "المدينة القديمة", "سيدي البرنوصي", "الرحمة", "أولاد زيان", "2 مارس", "الحي الصناعي", "عين البرجة", "سيدي بليوط", "بوشنتوف", "الكورنيش"),
    "الرباط" to listOf("اختر الحي", "جميع الأحياء", "أكدال", "حسان", "السويسي", "اليوسفية", "المحيط", "يعقوب المنصور", "الرياض", "حي الرياض", "التقدم", "العكاري", "حي النهضة", "الليمون", "أبي رقراق", "القبيبات", "المدينة القديمة", "أوداية"),
    "فاس" to listOf("اختر الحي", "جميع الأحياء", "فاس الجديد", "النرجس", "المدينة العتيقة", "المحطة الطرقية", "المرينيين", "الأطلس", "ظهر المهراز", "سايس", "بنسودة", "المسيرة", "أكدال", "جنان الورد", "عين قادوس", "ليراك", "باب فتوح"),
    "مراكش" to listOf("اختر الحي", "جميع الأحياء", "جيليز", "المحاميد", "المدينة المنورة", "سيدي غانم", "المسيرة", "الداوديات", "النخيل", "المدينة العتيقة", "أزلي", "سيدي يوسف بن علي", "تامنصورت", "الحي الصناعي", "جامع الفنا", "باب دكالة"),
    "طنجة" to listOf("اختر الحي", "جميع الأحياء", "طنجة المدينة", "بني مكادة", "المرس", "المنطقة الحرة", "مالاباطا", "الشرف", "الدرادب", "بوخالف", "مسنانة", "كاسابراطا", "السواني", "العوامة", "طنجة البالية", "الجبل الكبير", "أشقار"),
    "أكادير" to listOf("اختر الحي", "جميع الأحياء", "تالبورجت", "حي السلام", "المنصورية", "انزكان", "الهدى", "أنزا", "تيكيوين", "الحي المحمدي", "حي الداخلة", "بنسركاو", "تاسيلا", "الجرف"),
    "سلا" to listOf("اختر الحي", "جميع الأحياء", "بطانة", "سمعة", "حي السلام", "تابريكت", "سيدي موسى", "العيايدة", "حي كريمة", "الرحمة", "المنزه", "السهول", "بوقنادل", "الكرية"),
    "مكناس" to listOf("اختر الحي", "جميع الأحياء", "حمرية", "المدينة القديمة", "المدينة الجديدة", "الزيتون", "البساتين", "مرجان", "سيدي بابا", "الأمل", "الرياض", "حي النصر", "الوحدة"),
    "وجدة" to listOf("اختر الحي", "جميع الأحياء", "القدس", "حي السلام", "الأندلس", "لازاري", "سيدي يحيى", "المدينة القديمة", "الوفاق", "سيدي معافة", "الصفا", "الحي الجامعي"),
    "القنيطرة" to listOf("اختر الحي", "جميع الأحياء", "المدينة", "البطاح", "المهدية", "بير الرامي", "الساكنية", "المسيرة", "الحي الإداري", "الأمل"),
    "تطوان" to listOf("اختر الحي", "جميع الأحياء", "المدينة العتيقة", "المحنش", "عين خباز", "باب العقلة", "الوفاق"),
    "أسفي" to listOf("اختر الحي", "جميع الأحياء", "المدينة", "البياضية", "سيدي بوزيد", "الكورنيش", "الحي الصناعي", "حي المسيرة"),
    "محمدية" to listOf("اختر الحي", "جميع الأحياء", "المركز", "الياسمين", "العالية", "الشلالات", "المنصورية", "الكورنيش", "حي السلام"),
    "خريبكة" to listOf("اختر الحي", "جميع الأحياء", "الولفة", "القدس", "حي المسيرة", "الحي الإداري", "حي المعادن"),
    "الجديدة" to listOf("اختر الحي", "جميع الأحياء", "سيدي الضاوي", "حي المطار", "المدينة البرتغالية", "الحوزية", "سيدي بوزيد", "حي السلام", "المحطة", "الحي الصناعي"),
    "بني ملال" to listOf("اختر الحي", "جميع الأحياء", "الصومعة", "الرياض", "حي المسيرة", "الحي الإداري", "عين أسردون", "حي السلام", "المحطة", "الحي الصناعي"),
    "تمارة" to listOf("اختر الحي", "جميع الأحياء", "المركز", "المسيرة", "الهرهورة", "أم عزة", "حي النهضة", "تامسنا"),
    "الصخيرات" to listOf("اختر الحي", "جميع الأحياء", "المركز", "الشاطئ", "الكورنيش", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الناظور" to listOf("اختر الحي", "جميع الأحياء", "تاويمة", "لعري الشيخ", "أولاد ميمون", "فرخانة", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "العرائش" to listOf("اختر الحي", "جميع الأحياء", "المدينة القديمة", "الشاطئ", "ليكسوس", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الحسيمة" to listOf("اختر الحي", "جميع الأحياء", "سيدي عابد", "كالابونيتا", "الميناء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "تازة" to listOf("اختر الحي", "جميع الأحياء", "تازة العليا", "تازة السفلى", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "سطات" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "برشيد" to listOf("اختر الحي", "جميع الأحياء", "الحي الصناعي", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الخميسات" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "القصر الكبير" to listOf("اختر الحي", "جميع الأحياء", "المدينة القديمة", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الصويرة" to listOf("اختر الحي", "جميع الأحياء", "المدينة العتيقة", "الميناء", "الشاطئ", "سيدي كاوكي"),
    "ورزازات" to listOf("اختر الحي", "جميع الأحياء", "قصبة تاوريرت", "حي الأطلس", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "زاكورة" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الرشيدية" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "ميدلت" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "إفران" to listOf("اختر الحي", "جميع الأحياء", "حي الأطلس", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "آزرو" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "صفرو" to listOf("اختر الحي", "جميع الأحياء", "المدينة القديمة", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "تاونات" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "شفشاون" to listOf("اختر الحي", "جميع الأحياء", "المدينة العتيقة", "رأس الماء"),
    "وزان" to listOf("اختر الحي", "جميع الأحياء", "المدينة القديمة", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "أصيلة" to listOf("اختر الحي", "جميع الأحياء", "المدينة العتيقة", "الشاطئ", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "المضيق" to listOf("اختر الحي", "جميع الأحياء", "الكورنيش", "الميناء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الفنيدق" to listOf("اختر الحي", "جميع الأحياء", "الكورنيش", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "مرتيل" to listOf("اختر الحي", "جميع الأحياء", "الكورنيش", "الشاطئ", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "إمزورن" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "بني بوعياش" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "العيون" to listOf("اختر الحي", "جميع الأحياء", "فم الواد", "حي الوحدة", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الداخلة" to listOf("اختر الحي", "جميع الأحياء", "الميناء", "الحي الصناعي", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "كلميم" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "طانطان" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "سيدي إفني" to listOf("اختر الحي", "جميع الأحياء", "الشاطئ", "الميناء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "السمارة" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "بوجدور" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "تزنيت" to listOf("اختر الحي", "جميع الأحياء", "المدينة العتيقة", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "تارودانت" to listOf("اختر الحي", "جميع الأحياء", "المدينة العتيقة", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "إنزكان" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "آيت ملول" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الدشيرة الجهادية" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "بيوكرى" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "بركان" to listOf("اختر الحي", "جميع الأحياء", "الحي الصناعي", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "تاوريرت" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "جرادة" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "بوعرفة" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "فجيج" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "العروي" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "زايو" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "سلوان" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "بني أنصار" to listOf("اختر الحي", "جميع الأحياء", "الشاطئ", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الدريوش" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "سيدي قاسم" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "سيدي سليمان" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "سيدي يحيى الغرب" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "أزمور" to listOf("اختر الحي", "جميع الأحياء", "المدينة القديمة", "الشاطئ", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "سيدي بنور" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "اليوسفية" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "شيشاوة" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "قلعة السراغنة" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "ابن جرير" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الفقيه بن صالح" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "أزيلال" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "سوق السبت" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "وادي زم" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "أبي الجعد" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "خنيفرة" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "بنسليمان" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الحاجب" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "بوفكران" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "مولاي ادريس زرهون" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "تنغير" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "الجبهة" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية"),
    "مولاي يعقوب" to listOf("اختر الحي", "جميع الأحياء", "وسط المدينة", "حي السلام", "حي المسيرة", "الحي الإداري", "المحطة الطرقية")
)

data class DeliveryOffer(
    val id: String,
    val title: String,
    val city: String,
    val district: String,
    val roleType: String,
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

@SuppressLint("MissingPermission")
fun fetchCurrentLocation(context: Context, onResult: (Double?, Double?) -> Unit) {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    if (lm == null) {
        onResult(null, null)
        return
    }

    val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).filter {
        try { lm.isProviderEnabled(it) } catch (e: Exception) { false }
    }

    if (providers.isEmpty()) {
        Toast.makeText(context, "فعّل GPS من إعدادات الهاتف ثم عاود", Toast.LENGTH_LONG).show()
        try {
            context.startActivity(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: Exception) {}
        onResult(null, null)
        return
    }

    val lastKnown = try {
        providers.mapNotNull { lm.getLastKnownLocation(it) }.maxByOrNull { it.time }
    } catch (e: Exception) {
        null
    }

    if (lastKnown != null && System.currentTimeMillis() - lastKnown.time < 120_000) {
        onResult(lastKnown.latitude, lastKnown.longitude)
        return
    }

    val handler = Handler(Looper.getMainLooper())
    var finished = false
    val listeners = mutableListOf<LocationListener>()

    fun finish(loc: Location?) {
        if (finished) return
        finished = true
        listeners.forEach { try { lm.removeUpdates(it) } catch (e: Exception) {} }
        handler.removeCallbacksAndMessages(null)
        val result = loc ?: lastKnown
        onResult(result?.latitude, result?.longitude)
    }

    providers.forEach { provider ->
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) { finish(location) }
            @Suppress("DEPRECATION")
            override fun onStatusChanged(p: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(p: String) {}
            override fun onProviderDisabled(p: String) {}
        }
        listeners.add(listener)
        try {
            lm.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
        } catch (e: Exception) {}
    }

    handler.postDelayed({ finish(null) }, 20_000)
}

fun openLocationInGoogleMaps(context: Context, lat: Double, lng: Double, label: String) {
    val geoUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label)})")
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, geoUri)
                .setPackage("com.google.android.apps.maps")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (e: Exception) {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e2: Exception) {
            Toast.makeText(context, "الموقع: $lat, $lng", Toast.LENGTH_LONG).show()
        }
    }
}

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
    } catch (e: Exception) {}
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
                    district = obj.optString("district", "اختر الحي"),
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
    } catch (e: Exception) {}
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

@Composable
fun AmazonStyleLivlinkHeader(activeColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Text(
            text = "LIVLINK",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.width(128.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = "محل",
                tint = activeColor,
                modifier = Modifier.size(17.dp)
            )

            // السهم (طريق) اللي كيربط بين المحل والليفرور
            // الصورة: res/drawable/livlink_arrow.png
            Image(
                painter = painterResource(id = R.drawable.livlink_arrow),
                contentDescription = "ربط بين المحل والليفرور",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .weight(1f)
                    .height(22.dp)
                    .padding(horizontal = 4.dp)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                contentDescription = "دراجة نارية",
                tint = DriverOrangePrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "منصة ربط المحلات بالليفرورات",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
    val activeColor = if (isDriver) DriverOrangePrimary else ShopBlueDarkPrimary

    val screenBg = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBg = if (isDarkMode) NavySecondary else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var currentTab by remember { mutableIntStateOf(0) }
        var selectedCity by remember { mutableStateOf("جميع مدن المغرب") }
        var selectedDistrict by remember { mutableStateOf("اختر الحي") }

        var cityDropdownExpanded by remember { mutableStateOf(false) }
        var districtDropdownExpanded by remember { mutableStateOf(false) }
        var isDriverAvailable by remember { mutableStateOf(true) }

        var selectedOfferForDetails by remember { mutableStateOf<DeliveryOffer?>(null) }
        var selectedDriverForDetails by remember { mutableStateOf<DriverProfile?>(null) }

        var showEditProfileDialog by remember { mutableStateOf(false) }
        var profileName by remember {
            mutableStateOf(
                context.getSharedPreferences("livlink_prefs", Context.MODE_PRIVATE).getString("profile_name", null)
                    ?: (if (isDriver) "سائق توصيل معتمد" else "محل تجاري - ليفلينك")
            )
        }
        var profilePhone by remember {
            mutableStateOf(
                context.getSharedPreferences("livlink_prefs", Context.MODE_PRIVATE).getString("profile_phone", null)
                    ?: "0661234567"
            )
        }

        val offersList = remember {
            val saved = loadOffersFromPrefs(context)
            mutableStateListOf<DeliveryOffer>().apply {
                addAll(saved ?: emptyList())
            }
        }

        val driversList = remember {
            val saved = loadDriversFromPrefs(context)
            mutableStateListOf<DriverProfile>().apply {
                addAll(saved ?: emptyList())
            }
        }

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

        if (showEditProfileDialog) {
            var editName by remember { mutableStateOf(profileName) }
            var editPhone by remember { mutableStateOf(profilePhone) }
            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                title = { Text("تعديل الملف الشخصي", fontWeight = FontWeight.Bold, color = textColor) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("الاسم", color = subTextColor) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedBorderColor = activeColor,
                                unfocusedBorderColor = borderColor,
                                focusedLabelColor = activeColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("رقم الهاتف", color = subTextColor) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedBorderColor = activeColor,
                                unfocusedBorderColor = borderColor,
                                focusedLabelColor = activeColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editName.isBlank() || editPhone.isBlank()) {
                                Toast.makeText(context, "يرجى ملء الاسم والهاتف", Toast.LENGTH_SHORT).show()
                            } else {
                                profileName = editName.trim()
                                profilePhone = editPhone.trim()
                                context.getSharedPreferences("livlink_prefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("profile_name", profileName)
                                    .putString("profile_phone", profilePhone)
                                    .apply()
                                Toast.makeText(context, "تم حفظ التعديلات", Toast.LENGTH_SHORT).show()
                                showEditProfileDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                    ) {
                        Text("حفظ", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showEditProfileDialog = false },
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Text("إلغاء", color = textColor, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = if (isDarkMode) NavyPrimary else Color.White
            )
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
                            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // أيقونة التطبيق (بدل LV)
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    // نفس لون الشريط: خلفية الشعار تتحكم فيها الصورة نفسها
                                    .background(NavyPrimary)
                                    .border(
                                        width = 1.5.dp,
                                        brush = Brush.linearGradient(
                                            listOf(ShopBlueAccent, DriverOrangePrimary)
                                        ),
                                        shape = RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.livlink_logo),
                                    contentDescription = "LIVLINK",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(3.dp)
                                )
                            }

                            // الوضع الليلي
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, Color(0xFF475569), CircleShape)
                                    .clickable { onToggleDarkMode() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "تغيير الوضع",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // الشعار
                            AmazonStyleLivlinkHeader(activeColor = activeColor)

                            // وضع صاحب المحل  ⟷  رمز الربط  ⟷  وضع الليفرور
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // وضع صاحب المحل
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (!isDriver) ShopBlueDarkPrimary else Color(0xFF1E293B))
                                        .border(
                                            width = 1.5.dp,
                                            color = if (!isDriver) Color(0xFF93C5FD) else Color(0xFF334155),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onRoleChange("صاحب محل")
                                            Toast.makeText(context, "تم التبديل إلى وضع صاحب المحل", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = "وضع صاحب المحل",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // رمز الربط (Link) بين وضع المحل ووضع الليفرور
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(38.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
                                    // الخط كيمشي من لون المحل (أزرق) للون الليفرور (برتقالي) حسب اتجاه الشاشة
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(
                                                Brush.horizontalGradient(
                                                    if (isRtl) listOf(DriverOrangePrimary, ShopBlueAccent)
                                                    else listOf(ShopBlueAccent, DriverOrangePrimary)
                                                )
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(NavyPrimary)
                                            .border(1.dp, Color(0xFF64748B), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Link,
                                            contentDescription = "ربط بين المحل والليفرور",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }

                                // وضع الليفرور
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isDriver) DriverOrangePrimary else Color(0xFF1E293B))
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isDriver) Color(0xFFFED7AA) else Color(0xFF334155),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onRoleChange("ليفرور")
                                            Toast.makeText(context, "تم التبديل إلى وضع سائق التوصيل", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                                        contentDescription = "وضع سائق التوصيل",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = activeColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable {
                                    val newRole = if (isDriver) "صاحب محل" else "ليفرور"
                                    onRoleChange(newRole)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (isDriver) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(17.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isDriver) "وضع الليفرور (نشط)" else "وضع صاحب المحل (نشط)",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NavySecondary)
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
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
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = selectedCity,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = cityDropdownExpanded,
                                    onDismissRequest = { cityDropdownExpanded = false }
                                ) {
                                    MOROCCAN_CITIES.forEach { city ->
                                        DropdownMenuItem(
                                            text = { Text(city, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                                            onClick = {
                                                selectedCity = city
                                                selectedDistrict = "اختر الحي"
                                                cityDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NavySecondary)
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .clickable { districtDropdownExpanded = true }
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
                                            imageVector = Icons.Default.Map,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = selectedDistrict,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = districtDropdownExpanded,
                                    onDismissRequest = { districtDropdownExpanded = false }
                                ) {
                                    val availableDistricts = CITY_DISTRICTS_MAP[selectedCity] ?: listOf("اختر الحي", "جميع الأحياء")
                                    availableDistricts.forEach { district ->
                                        DropdownMenuItem(
                                            text = { Text(district, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                                            onClick = {
                                                selectedDistrict = district
                                                districtDropdownExpanded = false
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
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = Color.White,
                                            modifier = Modifier.size(if (index == 2) 26.dp else 22.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color.White,
                                    unselectedIconColor = Color.White,
                                    unselectedTextColor = Color.White,
                                    indicatorColor = activeColor
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
                    .background(screenBg)
            ) {
                val filteredOffers = offersList.filter { offer ->
                    val matchesCity = (selectedCity == "جميع مدن المغرب" || offer.city == selectedCity)
                    val matchesDistrict = (selectedDistrict == "اختر الحي" || selectedDistrict == "جميع الأحياء" || offer.district.contains(selectedDistrict))
                    matchesCity && matchesDistrict
                }

                when (currentTab) {
                    0 -> HomeTabContent(
                        isDriver = isDriver,
                        isDarkMode = isDarkMode,
                        activeColor = activeColor,
                        selectedCity = selectedCity,
                        selectedDistrict = selectedDistrict,
                        isDriverAvailable = isDriverAvailable,
                        onToggleDriverAvailability = { isDriverAvailable = !isDriverAvailable },
                        offers = filteredOffers,
                        onNavigateToOffers = { currentTab = 1 },
                        onNavigateToAdd = { currentTab = 2 },
                        onNavigateToDrivers = { currentTab = 3 },
                        onSelectOffer = { selectedOfferForDetails = it },
                        onDial = { dialPhone(it) },
                        onWhatsApp = { openWhatsApp(it) },
                        onOpenMap = { lat, lng, lbl, addr -> openMap(lat, lng, lbl, addr) }
                    )

                    1 -> OffersTabContent(
                        isDriver = isDriver,
                        isDarkMode = isDarkMode,
                        activeColor = activeColor,
                        selectedCity = selectedCity,
                        offers = filteredOffers,
                        onNavigateToAdd = { currentTab = 2 },
                        onSelectOffer = { selectedOfferForDetails = it },
                        onDial = { dialPhone(it) },
                        onWhatsApp = { openWhatsApp(it) },
                        onOpenMap = { lat, lng, lbl, addr -> openMap(lat, lng, lbl, addr) }
                    )

                    2 -> AddScreen(
                        isDriver = isDriver,
                        isDarkMode = isDarkMode,
                        onDismiss = { currentTab = 1 },
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
                                district = if (selectedDistrict == "اختر الحي") "جميع الأحياء" else selectedDistrict,
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

                            if (selectedCity != "جميع مدن المغرب" && selectedCity != city) {
                                selectedCity = city
                            }

                            Toast.makeText(context, "تم نشر إعلانك بنجاح في $city", Toast.LENGTH_SHORT).show()
                            currentTab = 1
                        }
                    )

                    3 -> DriversTabContent(
                        isDriver = isDriver,
                        isDarkMode = isDarkMode,
                        activeColor = activeColor,
                        selectedCity = selectedCity,
                        drivers = driversList.filter { selectedCity == "جميع مدن المغرب" || it.city == selectedCity },
                        onSelectDriver = { selectedDriverForDetails = it },
                        onDial = { dialPhone(it) },
                        onWhatsApp = { openWhatsApp(it) }
                    )

                    4 -> ProfileTabContent(
                        isDriver = isDriver,
                        isDarkMode = isDarkMode,
                        activeColor = activeColor,
                        selectedCity = selectedCity,
                        userName = profileName,
                        userPhone = profilePhone,
                        isDriverAvailable = isDriverAvailable,
                        onToggleDriverAvailability = { isDriverAvailable = !isDriverAvailable },
                        onSwitchRole = {
                            val nextRole = if (isDriver) "صاحب محل" else "ليفرور"
                            onRoleChange(nextRole)
                        },
                        onOpenEditDialog = { showEditProfileDialog = true }
                    )
                }

                selectedOfferForDetails?.let { offer ->
                    val dialogBg = if (isDarkMode) NavyPrimary else Color(0xFFFFFFFF)
                    val dialogText = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    AlertDialog(
                        onDismissRequest = { selectedOfferForDetails = null },
                        title = { Text(text = offer.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = dialogText) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "نوع الإعلان: ${offer.roleType}", fontWeight = FontWeight.Bold, color = activeColor, fontSize = 13.sp)
                                Text(text = "المدينة والحي: ${offer.city} • ${offer.district}", fontSize = 12.5.sp, color = dialogText)
                                Text(text = "تفاصيل الإعلان: ${offer.details}", fontSize = 12.sp, lineHeight = 17.sp, color = dialogText)
                                Text(text = "رقم الهاتف: ${offer.phone}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = dialogText)
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
                                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("واتساب", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                if (offer.roleType == "طلب توصيل" && ((offer.latitude != null && offer.longitude != null) || !offer.locationAddress.isNullOrBlank())) {
                                    Button(
                                        onClick = {
                                            openMap(offer.latitude, offer.longitude, offer.title, offer.locationAddress)
                                            selectedOfferForDetails = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ShopBlueDarkPrimary)
                                    ) {
                                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("الخريطة", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    dialPhone(offer.phone)
                                    selectedOfferForDetails = null
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = dialogText),
                                border = BorderStroke(1.dp, borderColor)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = dialogText)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اتصال", color = dialogText, fontWeight = FontWeight.Bold)
                            }
                        },
                        containerColor = dialogBg
                    )
                }

                selectedDriverForDetails?.let { driver ->
                    val dialogBg = if (isDarkMode) NavyPrimary else Color(0xFFFFFFFF)
                    val dialogText = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    AlertDialog(
                        onDismissRequest = { selectedDriverForDetails = null },
                        title = { Text(text = driver.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = dialogText) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "المدينة: ${driver.city}", fontSize = 13.sp, color = dialogText)
                                Text(text = "وسيلة النقل: ${driver.vehicle}", fontSize = 13.sp, color = dialogText)
                                Text(text = "سنوات الخبرة: ${driver.experience}", fontSize = 13.sp, color = dialogText)
                                Text(text = "التوصيلات المنجزة: ${driver.completedTrips} توصيلة", fontSize = 13.sp, color = dialogText, fontWeight = FontWeight.Bold)
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
                                Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("واتساب", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    dialPhone(driver.phone)
                                    selectedDriverForDetails = null
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = dialogText),
                                border = BorderStroke(1.dp, borderColor)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = dialogText)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اتصال مباشر", color = dialogText, fontWeight = FontWeight.Bold)
                            }
                        },
                        containerColor = dialogBg
                    )
                }
            }
        }
    }
}

@Composable
fun AddScreen(
    isDriver: Boolean,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    initialCity: String = "جميع مدن المغرب",
    onAddSubmit: (title: String, city: String, phone: String, details: String, type: String, lat: Double?, lng: Double?, address: String?) -> Unit
) {
    val context = LocalContext.current
    var selectedType by remember(isDriver) { mutableStateOf(if (isDriver) "عرض توصيل" else "طلب توصيل") }
    var titleText by remember { mutableStateOf("") }
    var cityText by remember { mutableStateOf(if (initialCity == "جميع مدن المغرب") "الدار البيضاء" else initialCity) }
    var phoneText by remember { mutableStateOf("") }
    var detailsText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // حالة الموقع
    var latValue by remember { mutableStateOf<Double?>(null) }
    var lngValue by remember { mutableStateOf<Double?>(null) }
    var addressValue by remember { mutableStateOf<String?>(null) }
    var isLocating by remember { mutableStateOf(false) }
    var showManualDialog by remember { mutableStateOf(false) }
    var manualAddressText by remember { mutableStateOf("") }

    val screenBg = if (isDarkMode) NavyPrimary else Color(0xFFF8FAFC)
    val cardBg = if (isDarkMode) NavySecondary else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)

    fun startLocating() {
        isLocating = true
        fetchCurrentLocation(context) { lat, lng ->
            isLocating = false
            if (lat != null && lng != null) {
                latValue = lat
                lngValue = lng
                Toast.makeText(context, "تم تحديد الموقع بنجاح", Toast.LENGTH_SHORT).show()
                openLocationInGoogleMaps(context, lat, lng, titleText.ifBlank { "موقع المحل" })
            } else {
                Toast.makeText(context, "ما قدرناش نحددو الموقع، تأكد أن GPS مفعل وعاود", Toast.LENGTH_LONG).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            startLocating()
        } else {
            Toast.makeText(context, "خاصك تقبل صلاحية الموقع باش نحددو موقع المحل", Toast.LENGTH_LONG).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = screenBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إضافة $selectedType جديد",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textColor
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = textColor)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "نوع الإعلان المراد نشره:", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = textColor)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { selectedType = "طلب توصيل" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == "طلب توصيل") ShopBlueDarkPrimary else (if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0))
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("طلب توصيل (محل)", color = if (selectedType == "طلب توصيل" || isDarkMode) Color.White else Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { selectedType = "عرض توصيل" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == "عرض توصيل") DriverOrangePrimary else (if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0))
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("عرض توصيل (ليفرور)", color = if (selectedType == "عرض توصيل" || isDarkMode) Color.White else Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text("اسم المحل ونوع السلع *", color = subTextColor) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedBorderColor = if (isDriver) DriverOrangePrimary else ShopBlueDarkPrimary,
                            unfocusedBorderColor = borderColor,
                            focusedLabelColor = if (isDriver) DriverOrangePrimary else ShopBlueDarkPrimary,
                            unfocusedLabelColor = subTextColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phoneText,
                        onValueChange = { phoneText = it },
                        label = { Text("رقم الهاتف أو الواتساب للتواصل المباشر *", color = subTextColor) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedBorderColor = if (isDriver) DriverOrangePrimary else ShopBlueDarkPrimary,
                            unfocusedBorderColor = borderColor,
                            focusedLabelColor = if (isDriver) DriverOrangePrimary else ShopBlueDarkPrimary,
                            unfocusedLabelColor = subTextColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = detailsText,
                        onValueChange = { detailsText = it },
                        label = { Text("تفاصيل إضافية (الأحياء، شروط التوصيل، التوقيت)", color = subTextColor) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedBorderColor = if (isDriver) DriverOrangePrimary else ShopBlueDarkPrimary,
                            unfocusedBorderColor = borderColor,
                            focusedLabelColor = if (isDriver) DriverOrangePrimary else ShopBlueDarkPrimary,
                            unfocusedLabelColor = subTextColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    if (!isDriver && selectedType == "طلب توصيل") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) NavyPrimary else Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "موقع المحل على الخريطة (اختياري لصاحب المحل)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = textColor
                                    )
                                }
                                Text(
                                    text = "يسمح بتحديد إحداثيات موقع محلك الجغرافي لتسهيل وصول الليفرورات إليك عبر الخريطة.",
                                    fontSize = 12.sp,
                                    color = subTextColor,
                                    lineHeight = 17.sp
                                )

                                if (latValue != null && lngValue != null) {
                                    Text(
                                        text = "✓ تم حفظ الموقع: ${"%.5f".format(latValue)}, ${"%.5f".format(lngValue)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                }
                                if (!addressValue.isNullOrBlank()) {
                                    Text(
                                        text = "✓ العنوان: $addressValue",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val hasPermission =
                                                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                            if (hasPermission) {
                                                startLocating()
                                            } else {
                                                permissionLauncher.launch(
                                                    arrayOf(
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                    )
                                                )
                                            }
                                        },
                                        enabled = !isLocating,
                                        colors = ButtonDefaults.buttonColors(containerColor = ShopBlueAccent),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isLocating) "جاري التحديد..." else "تحديد عبر GPS",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            manualAddressText = addressValue ?: ""
                                            showManualDialog = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                                        border = BorderStroke(1.dp, borderColor)
                                    ) {
                                        Text("إدخال يدوي", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (titleText.isNotBlank() && phoneText.isNotBlank()) {
                                onAddSubmit(titleText, cityText, phoneText, detailsText, selectedType, latValue, lngValue, addressValue)
                            } else {
                                Toast.makeText(context, "يرجى ملء جميع الحقول المطلوبة (*)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedType == "عرض توصيل") DriverOrangePrimary else ShopBlueDarkPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("نشر $selectedType", fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color.White)
                    }
                }
            }
        }
    }

    if (showManualDialog) {
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("عنوان المحل", fontWeight = FontWeight.Bold, color = textColor) },
            text = {
                OutlinedTextField(
                    value = manualAddressText,
                    onValueChange = { manualAddressText = it },
                    label = { Text("مثال: شارع محمد الخامس، المعاريف، الدار البيضاء", color = subTextColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = ShopBlueDarkPrimary,
                        unfocusedBorderColor = borderColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        addressValue = manualAddressText.trim().ifBlank { null }
                        showManualDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopBlueDarkPrimary)
                ) {
                    Text("حفظ", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showManualDialog = false }) {
                    Text("إلغاء", color = textColor, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = if (isDarkMode) NavyPrimary else Color.White
        )
    }
}

@Composable
fun HomeTabContent(
    isDriver: Boolean,
    isDarkMode: Boolean,
    activeColor: Color,
    selectedCity: String,
    selectedDistrict: String,
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
    val cardBg = if (isDarkMode) NavySecondary else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = activeColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isDriver) "أهلاً بك يا كابتن التوصيل!" else "مرحباً بك يا صاحب المحل!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
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
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isDriver) "تصفح طلبيات المحلات والمطاعم بجميع أحياء ومدن المغرب وتواصل فوراً مع أصحابها."
                        else "اعثر على ليفرور موثوق لمحلك في $selectedCity (${if (selectedDistrict == "اختر الحي") "جميع الأحياء" else selectedDistrict}) وقم بإرسال طلبيات زبائنك بسرعة وأمان.",
                        fontSize = 13.5.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onNavigateToAdd,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDriver) "نشر عرض جاهزية للتوصيل" else "إضافة طلب توصيل جديد لمحلك",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (isDriver) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDriverAvailable) Color(0xFF064E3B) else Color(0xFF334155)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(if (isDriverAvailable) Color(0xFF4ADE80) else Color(0xFF94A3B8)))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isDriverAvailable) "حالتك: متاح حالياً للتوصيل" else "حالتك: غير متاح حالياً",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isDriverAvailable) "المحلات في $selectedCity يمكنها الاتصال بك الآن" else "إعلاناتك متوقفة مؤقتاً",
                                    fontSize = 12.5.sp,
                                    color = Color.White
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

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToOffers() }
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.ListAlt, contentDescription = null, tint = textColor, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = if (isDriver) "طلبيات المحلات" else "سوق العروض", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = textColor)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToAdd() }
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = textColor, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = if (isDriver) "نشر عرض" else "طلب ليفرور", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = textColor)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDrivers() }
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box), contentDescription = null, tint = textColor, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "الليفرورات", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = textColor)
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isDriver) "أحدث طلبات التوصيل ($selectedCity - $selectedDistrict):" else "أحدث الإعلانات ($selectedCity):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = textColor
                )
                TextButton(onClick = onNavigateToOffers) {
                    Text(text = "عرض الكل", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                }
            }
        }

        if (offers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Inbox, contentDescription = null, tint = textColor, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "لا توجد إعلانات حالياً في $selectedCity ($selectedDistrict)", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "كن أول من يضيف إعلاناً في منطقتك عبر زر الإضافة أسفله", fontSize = 12.sp, color = subTextColor, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(offers.take(3)) { offer ->
                OfferItemCard(
                    offer = offer,
                    isDarkMode = isDarkMode,
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

@Composable
fun OffersTabContent(
    isDriver: Boolean,
    isDarkMode: Boolean,
    activeColor: Color,
    selectedCity: String,
    offers: List<DeliveryOffer>,
    onNavigateToAdd: () -> Unit,
    onSelectOffer: (DeliveryOffer) -> Unit,
    onDial: (String) -> Unit,
    onWhatsApp: (String) -> Unit,
    onOpenMap: (Double?, Double?, String, String?) -> Unit
) {
    var filterType by remember { mutableStateOf("الكل") }
    val cardBg = if (isDarkMode) NavySecondary else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "سوق العروض والطلبيات", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                    Text(text = "المدينة: $selectedCity (${displayOffers.size} متاح)", fontSize = 12.sp, color = subTextColor)
                }
                Button(
                    onClick = onNavigateToAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = activeColor),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("الكل", "طلب توصيل", "عرض توصيل").forEach { chip ->
                    val isSelected = filterType == chip
                    ElevatedFilterChip(
                        selected = isSelected,
                        onClick = { filterType = chip },
                        label = { Text(chip, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else textColor) },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = activeColor,
                            selectedLabelColor = Color.White,
                            containerColor = cardBg,
                            labelColor = textColor
                        )
                    )
                }
            }
        }

        if (displayOffers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "لا توجد إعلانات مطابقة حالياً", fontSize = 13.5.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = textColor)
                    }
                }
            }
        } else {
            items(displayOffers) { offer ->
                OfferItemCard(
                    offer = offer,
                    isDarkMode = isDarkMode,
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

@Composable
fun DriversTabContent(
    isDriver: Boolean,
    isDarkMode: Boolean,
    activeColor: Color,
    selectedCity: String,
    drivers: List<DriverProfile>,
    onSelectDriver: (DriverProfile) -> Unit,
    onDial: (String) -> Unit,
    onWhatsApp: (String) -> Unit
) {
    val cardBg = if (isDarkMode) NavySecondary else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

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
                color = textColor
            )
            Text(
                text = "تواصل مباشرة مع الليفرور المتاح في $selectedCity بدون أي وسيط",
                fontSize = 12.5.sp,
                color = subTextColor
            )
        }

        if (drivers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "لا يوجد سائقون مسجلون حالياً في $selectedCity",
                            textAlign = TextAlign.Center,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "عند نشر عروض التوصيل ستظهر هنا تلقائياً",
                            fontSize = 12.sp,
                            color = subTextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(drivers) { driver ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectDriver(driver) }
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (driver.isAvailable) Color(0xFF064E3B) else Color(0xFF334155)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeliveryDining,
                                        contentDescription = null,
                                        tint = if (driver.isAvailable) Color(0xFF4ADE80) else Color.White,
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
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (driver.isAvailable) Color(0xFF064E3B) else Color(0xFF334155))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (driver.isAvailable) "متاح" else "مشغول",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (driver.isAvailable) Color(0xFF4ADE80) else Color.White
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${driver.city} • ${driver.vehicle} (${driver.experience})",
                                        fontSize = 12.5.sp,
                                        color = subTextColor
                                    )
                                    Text(
                                        text = "${driver.completedTrips} توصيلة ناجحة",
                                        fontSize = 12.sp,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الهاتف: ${driver.phone}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = textColor
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { onWhatsApp(driver.phone) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("واتساب", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Button(
                                    onClick = { onDial(driver.phone) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("اتصال", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTabContent(
    isDriver: Boolean,
    isDarkMode: Boolean,
    activeColor: Color,
    selectedCity: String,
    userName: String,
    userPhone: String,
    isDriverAvailable: Boolean,
    onToggleDriverAvailability: () -> Unit,
    onSwitchRole: () -> Unit,
    onOpenEditDialog: () -> Unit
) {
    val cardBg = if (isDarkMode) NavySecondary else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = cardBg),
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
                            .background(activeColor.copy(alpha = 0.3f))
                            .border(2.dp, textColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDriver) Icons.Default.DeliveryDining else Icons.Default.Store,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = userName, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = textColor)
                    Text(
                        text = if (isDriver) "حساب سائق توصيل • $selectedCity" else "حساب صاحب محل تجاري • $selectedCity",
                        fontSize = 12.5.sp,
                        color = subTextColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onSwitchRole,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                            border = BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp), tint = textColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isDriver) "التبديل لوضع المحل" else "التبديل لوضع الليفرور", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = textColor)
                        }
                        Button(
                            onClick = onOpenEditDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = activeColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تعديل", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "إعدادات ومعلومات الحساب:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = textColor
                    )

                    Divider(color = borderColor)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "الاسم المعتمد في المنصة:", fontSize = 13.sp, color = subTextColor)
                        Text(text = userName, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = textColor)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "رقم الهاتف للتواصل المباشر:", fontSize = 13.sp, color = subTextColor)
                        Text(text = userPhone, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = textColor)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "المدينة المحددة حالياً:", fontSize = 13.sp, color = subTextColor)
                        Text(text = selectedCity, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = textColor)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "نموذج الاستخدام المعتمد:", fontSize = 13.sp, color = subTextColor)
                        Text(text = "تطبيق مجاني 100%", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFF16A34A))
                    }
                }
            }
        }
    }
}

@Composable
fun OfferItemCard(
    offer: DeliveryOffer,
    isDarkMode: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    onDial: () -> Unit,
    onWhatsApp: () -> Unit,
    onOpenMap: ((Double?, Double?, String, String?) -> Unit)? = null
) {
    val isDriverOffer = offer.roleType == "عرض توصيل"
    val cardBg = if (isDarkMode) NavySecondary else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isDriverOffer) ShopBlueDarkPrimary else DriverOrangePrimary)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isDriverOffer) {
                                Icon(painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box), contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            } else {
                                Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isDriverOffer) "عرض توصيل (ليفرور)" else "طلب توصيل (محل)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${offer.city} • ${offer.district}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                Text(
                    text = offer.timeAgo,
                    fontSize = 12.sp,
                    color = subTextColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = offer.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.5.sp,
                color = textColor
            )

            if (isDriverOffer && offer.vehicleType.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DriverOrangePrimary.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_delivery_motorcycle_box), contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "وسيلة النقل: ${offer.vehicleType}",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = offer.details,
                fontSize = 13.5.sp,
                color = subTextColor,
                lineHeight = 19.sp
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
                    fontSize = 13.5.sp,
                    color = textColor
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onWhatsApp,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "واتساب", tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("واتساب", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = onDial,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "اتصال", tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("اتصال", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    if (!isDriverOffer && ((offer.latitude != null && offer.longitude != null) || !offer.locationAddress.isNullOrBlank()) && onOpenMap != null) {
                        Button(
                            onClick = { onOpenMap(offer.latitude, offer.longitude, offer.title, offer.locationAddress) },
                            colors = ButtonDefaults.buttonColors(containerColor = ShopBlueDarkPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = "موقع المحل", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("الموقع", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
