/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import {
  Store,
  Bike,
  Truck,
  Phone,
  MessageCircle,
  MapPin,
  Map as MapIcon,
  Plus,
  Users,
  CheckCircle,
  Moon,
  Sun,
  ChevronDown,
  ShieldCheck,
  AlertCircle,
  Clock,
  Smartphone,
  Maximize2,
  FileCode,
  X,
  ListFilter,
  Check,
  Navigation
} from 'lucide-react';

interface DeliveryOffer {
  id: string;
  title: string;
  city: string;
  district: string;
  roleType: 'طلب توصيل' | 'عرض توصيل';
  phone: string;
  timeAgo: string;
  details: string;
  vehicleType?: string;
}

interface DriverProfile {
  id: string;
  name: string;
  city: string;
  vehicle: string;
  phone: string;
  isAvailable: boolean;
  experience: string;
}

const MOROCCAN_CITIES = [
  'جميع مدن المغرب',
  'الدار البيضاء',
  'الرباط',
  'فاس',
  'طنجة',
  'مراكش',
  'أكادير',
  'مكناس',
  'وجدة',
  'القنيطرة',
  'تطوان',
  'تمارة',
  'آسفي',
  'المحمدية',
  'الناظور',
  'العيون',
  'بني ملال',
  'الجديدة',
  'تازة',
  'خريبكة'
];

export default function App() {
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [currentTab, setCurrentTab] = useState<number>(0); // 0: Home, 1: Offers, 2: Map, 3: Add, 4: Drivers
  const [selectedRole, setSelectedRole] = useState<'صاحب محل' | 'ليفرور'>('صاحب محل');
  const [selectedCity, setSelectedCity] = useState<string>('جميع مدن المغرب');
  const [isPhoneFrame, setIsPhoneFrame] = useState(true);
  const [showCodeInspector, setShowCodeInspector] = useState(false);
  const [selectedInspectorFile, setSelectedInspectorFile] = useState<'NativeHomeScreenNew' | 'AddScreen' | 'buildGradle' | 'workflow'>('NativeHomeScreenNew');

  // Add Screen Form State
  const [addType, setAddType] = useState<'طلب توصيل' | 'عرض توصيل'>('طلب توصيل');
  const [addTitle, setAddTitle] = useState('');
  const [addCity, setAddCity] = useState('الدار البيضاء');
  const [addPhone, setAddPhone] = useState('');
  const [addDetails, setAddDetails] = useState('');
  const [addSuccessMessage, setAddSuccessMessage] = useState(false);

  // Offers Data
  const [offers, setOffers] = useState<DeliveryOffer[]>([
    {
      id: '1',
      title: 'محل إلكترونيات وأكسسوارات الهواتف',
      city: 'الدار البيضاء',
      district: 'المعاريف / سيدي معروف',
      roleType: 'طلب توصيل',
      phone: '0661001122',
      timeAgo: 'منذ 15 دقيقة',
      details: 'مطلوب ليفرور لتوصيل طلبيات يومية ثابتة ومضمونة من 10 صباحاً إلى 7 مساءً.'
    },
    {
      id: '2',
      title: 'ليفرور بموتور خاص (متاح للتوصيل السريع)',
      city: 'الرباط',
      district: 'أكدال / حسان / السويسي',
      roleType: 'عرض توصيل',
      phone: '0670334455',
      timeAgo: 'منذ 30 دقيقة',
      details: 'سائق دراجة نارية سريع وموثوق متاح لتغطية طلبات المطاعم والمحلات بمدينة الرباط وسلا ونواحيها.',
      vehicleType: 'دراجة نارية'
    },
    {
      id: '3',
      title: 'مطعم وسناك مأكولات سريعة',
      city: 'فاس',
      district: 'فاس الجديد / النرجس',
      roleType: 'طلب توصيل',
      phone: '0652889900',
      timeAgo: 'منذ ساعة',
      details: 'نبحث عن سائقين لخدمة توصيل وجبات الغداء والعشاء بانتظام.'
    },
    {
      id: '4',
      title: 'مكتبة ومستلزمات مكتبية وطباعة',
      city: 'مراكش',
      district: 'جيليز / المحاميد',
      roleType: 'طلب توصيل',
      phone: '0663112233',
      timeAgo: 'منذ ساعتين',
      details: 'توصيل طرود خفيفة ومطبوعات للمدارس والشركات داخل مراكش.'
    },
    {
      id: '5',
      title: 'ليفرور محترف بسيارة نفعية (Kangoo)',
      city: 'طنجة',
      district: 'طنجة المدينة / بني مكادة',
      roleType: 'عرض توصيل',
      phone: '0664778899',
      timeAgo: 'منذ 3 ساعات',
      details: 'مستعد لتوصيل الطرود المتوسطة والسلع الكبيرة في طنجة والمنطقة الحرة بكل أمانة.',
      vehicleType: 'سيارة نفعية'
    },
    {
      id: '6',
      title: 'محل بيع الملابس الجاهزة والأحذية',
      city: 'أكادير',
      district: 'تالبورجت / السلام',
      roleType: 'طلب توصيل',
      phone: '0655667788',
      timeAgo: 'منذ 4 ساعات',
      details: 'توصيل طلبيات زبناء التجارة الإلكترونية داخل مدينة أكادير وإنزكان.'
    }
  ]);

  // Drivers Data
  const drivers: DriverProfile[] = [
    { id: 'd1', name: 'يوسف العمراني', city: 'الدار البيضاء', vehicle: 'دراجة نارية', phone: '0661122334', isAvailable: true, experience: '3 سنوات' },
    { id: 'd2', name: 'كريم السبتي', city: 'الرباط', vehicle: 'دراجة نارية', phone: '0672233445', isAvailable: true, experience: 'سنتان' },
    { id: 'd3', name: 'حمزة التازي', city: 'فاس', vehicle: 'دراجة نارية', phone: '0653344556', isAvailable: true, experience: '4 سنوات' },
    { id: 'd4', name: 'أنس البورقادي', city: 'طنجة', vehicle: 'سيارة خفيفة', phone: '0664455667', isAvailable: false, experience: '5 سنوات' },
    { id: 'd5', name: 'رشيد المرابط', city: 'مراكش', vehicle: 'دراجة نارية', phone: '0675566778', isAvailable: true, experience: 'سنة واحدة' },
    { id: 'd6', name: 'طارق بنعلي', city: 'أكادير', vehicle: 'دراجة نارية', phone: '0656677889', isAvailable: true, experience: 'سنتان' },
    { id: 'd7', name: 'عمر القادري', city: 'وجدة', vehicle: 'دراجة نارية', phone: '0667889900', isAvailable: true, experience: '3 سنوات' }
  ];

  // Filtered lists
  const filteredOffers = offers.filter(
    (o) => selectedCity === 'جميع مدن المغرب' || o.city === selectedCity
  );

  const filteredDrivers = drivers.filter(
    (d) => selectedCity === 'جميع مدن المغرب' || d.city === selectedCity
  );

  const handleAddNewSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!addTitle.trim() || !addPhone.trim()) return;

    const newOffer: DeliveryOffer = {
      id: Date.now().toString(),
      title: addTitle.trim(),
      city: addCity,
      district: 'وسط المدينة',
      roleType: addType,
      phone: addPhone.trim(),
      timeAgo: 'الآن',
      details: addDetails.trim() || 'لا توجد تفاصيل إضافية'
    };

    setOffers([newOffer, ...offers]);
    setAddSuccessMessage(true);
    setTimeout(() => {
      setAddSuccessMessage(false);
      setAddTitle('');
      setAddPhone('');
      setAddDetails('');
      setCurrentTab(1); // Jump to offers tab
    }, 1500);
  };

  return (
    <div className={`min-h-screen ${isDarkMode ? 'bg-slate-950 text-slate-100' : 'bg-slate-100 text-slate-900'} font-sans flex flex-col`}>
      {/* Top AI Studio Header Bar */}
      <header className="bg-slate-900 border-b border-slate-800 px-4 py-2.5 flex items-center justify-between text-white text-xs select-none shadow-sm">
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
          <span className="font-bold tracking-wide text-sm bg-gradient-to-r from-red-500 to-rose-400 bg-clip-text text-transparent">
            LIVLINK MA
          </span>
          <span className="text-slate-400 hidden sm:inline">| منصة ربط المحلات بالليفرورات (تغطية وطنية مغربية 100٪ مجانية)</span>
        </div>

        <div className="flex items-center gap-2">
          {/* Switch Phone Frame Mode */}
          <button
            onClick={() => setIsPhoneFrame(!isPhoneFrame)}
            className="flex items-center gap-1.5 px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-200 transition"
            title="تبديل وضع الهاتف / الشاشة الكاملة"
          >
            {isPhoneFrame ? <Maximize2 className="w-3.5 h-3.5" /> : <Smartphone className="w-3.5 h-3.5" />}
            <span className="hidden md:inline">{isPhoneFrame ? 'ملء الشاشة' : 'إطار الهاتف'}</span>
          </button>

          {/* Code Inspector button */}
          <button
            onClick={() => setShowCodeInspector(true)}
            className="flex items-center gap-1.5 px-2.5 py-1 rounded bg-red-600 hover:bg-red-700 text-white font-medium transition"
          >
            <FileCode className="w-3.5 h-3.5" />
            <span>ملفات Android المُصلحة</span>
          </button>
        </div>
      </header>

      {/* Main Container */}
      <main className="flex-1 flex items-center justify-center p-2 sm:p-4 md:p-6 overflow-auto">
        <div
          className={`w-full transition-all duration-300 ${
            isPhoneFrame
              ? 'max-w-[420px] rounded-[38px] shadow-2xl border-[10px] border-slate-900 overflow-hidden ring-1 ring-slate-800'
              : 'max-w-4xl rounded-2xl shadow-xl border border-slate-300 dark:border-slate-800 overflow-hidden'
          } ${isDarkMode ? 'bg-slate-900' : 'bg-white'} flex flex-col h-[780px] max-h-[92vh]`}
        >
          {/* Mobile Status Bar Simulation */}
          <div className="bg-slate-900 text-white px-5 pt-2 pb-1 flex justify-between items-center text-[11px] font-medium tracking-tight">
            <span>12:30</span>
            <div className="w-20 h-4 bg-slate-950 rounded-full mx-auto -mt-1 hidden sm:block"></div>
            <div className="flex items-center gap-1.5">
              <span>5G</span>
              <span>100%</span>
            </div>
          </div>

          {/* LIVLINK Top Bar */}
          <div className="bg-slate-900 text-white px-4 py-3 border-b border-slate-800">
            {/* Top row: Brand & Dark Toggle */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2.5">
                <div className="w-9 h-9 rounded-lg bg-red-600 flex items-center justify-center font-black text-white text-base shadow-md">
                  LV
                </div>
                <div>
                  <h1 className="text-base font-black tracking-wide text-white leading-tight">LIVLINK</h1>
                  <p className="text-[11px] text-slate-300 font-medium">منصة ربط المحلات بالليفرورات</p>
                </div>
              </div>

              {/* Dark mode button */}
              <button
                onClick={() => setIsDarkMode(!isDarkMode)}
                className="w-8 h-8 rounded-full bg-slate-800 hover:bg-slate-700 flex items-center justify-center text-slate-200 transition"
                title="الوضع الليلي / النهاري"
              >
                {isDarkMode ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4 text-slate-300" />}
              </button>
            </div>

            {/* Nationwide definition badge */}
            <div className="mt-2.5 bg-slate-800/80 rounded-lg px-2.5 py-1.5 flex items-center gap-2 text-[11px] text-slate-200 border border-slate-700/50">
              <ShieldCheck className="w-4 h-4 text-emerald-400 shrink-0" />
              <span>منصة وطنية مغربية مجانية 100٪ بدون اشتراكات أو عمولات</span>
            </div>

            {/* Roles & City Selector Row */}
            <div className="mt-2.5 grid grid-cols-2 gap-2">
              {/* Role Toggle */}
              <div className="bg-slate-800 p-0.5 rounded-lg flex text-xs">
                <button
                  onClick={() => setSelectedRole('صاحب محل')}
                  className={`flex-1 py-1 rounded-md font-bold transition text-center ${
                    selectedRole === 'صاحب محل' ? 'bg-red-600 text-white shadow' : 'text-slate-300 hover:text-white'
                  }`}
                >
                  صاحب محل
                </button>
                <button
                  onClick={() => setSelectedRole('ليفرور')}
                  className={`flex-1 py-1 rounded-md font-bold transition text-center ${
                    selectedRole === 'ليفرور' ? 'bg-red-600 text-white shadow' : 'text-slate-300 hover:text-white'
                  }`}
                >
                  ليفرور
                </button>
              </div>

              {/* City Dropdown */}
              <div className="relative">
                <select
                  value={selectedCity}
                  onChange={(e) => setSelectedCity(e.target.value)}
                  className="w-full bg-slate-800 text-white text-xs font-medium rounded-lg px-2 py-1.5 pr-2 appearance-none border border-slate-700 focus:outline-none focus:ring-1 focus:ring-red-500 cursor-pointer"
                >
                  {MOROCCAN_CITIES.map((c) => (
                    <option key={c} value={c} className="bg-slate-900 text-white">
                      {c}
                    </option>
                  ))}
                </select>
                <ChevronDown className="w-3.5 h-3.5 text-slate-400 absolute left-2 top-2.5 pointer-events-none" />
              </div>
            </div>
          </div>

          {/* App Body Content based on Active Tab */}
          <div className={`flex-1 overflow-y-auto p-3.5 ${isDarkMode ? 'bg-slate-950' : 'bg-slate-50'}`}>
            {/* TAB 0: HOME */}
            {currentTab === 0 && (
              <div className="space-y-3.5">
                {/* Hero Role Card */}
                <div
                  className={`rounded-2xl p-4 text-white shadow-md relative overflow-hidden ${
                    selectedRole === 'صاحب محل'
                      ? 'bg-gradient-to-br from-red-600 to-rose-700'
                      : 'bg-gradient-to-br from-slate-900 to-slate-800 border border-slate-700'
                  }`}
                >
                  <div className="flex justify-between items-start">
                    <span className="text-xs font-bold bg-white/20 px-2 py-0.5 rounded-full backdrop-blur-sm">
                      {selectedRole === 'صاحب محل' ? 'خدمة أصحاب المحلات' : 'خدمة سائقي التوصيل'}
                    </span>
                    <span className="text-[10px] bg-emerald-500 text-white font-bold px-2 py-0.5 rounded">مجاني 100٪</span>
                  </div>

                  <h2 className="text-base font-extrabold mt-2">
                    {selectedRole === 'صاحب محل' ? 'عندك سلعة ومحتاج ليفرور ثقة؟' : 'عندك دراجة نارية أو سيارة وبغيتي تخدم؟'}
                  </h2>

                  <p className="text-xs mt-1 text-slate-100 leading-relaxed opacity-95">
                    {selectedRole === 'صاحب محل'
                      ? `انشر طلبات التوصيل الخاصة بمحلك وتواصل مباشرة مع ليفرورات ${selectedCity} بدون وسيط وبدون اقتطاع أي عمولة.`
                      : `اعثر على عروض التوصيل للمحلات والمطاعم في ${selectedCity} وتواصل فوراً مع أصحابها عبر الواتساب.`}
                  </p>

                  <div className="mt-3.5 flex gap-2">
                    <button
                      onClick={() => setCurrentTab(3)}
                      className={`flex-1 py-2 px-3 rounded-xl font-bold text-xs flex items-center justify-center gap-1.5 shadow transition ${
                        selectedRole === 'صاحب محل' ? 'bg-slate-900 hover:bg-slate-800 text-white' : 'bg-red-600 hover:bg-red-700 text-white'
                      }`}
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>{selectedRole === 'صاحب محل' ? 'نشر طلب توصيل جديد' : 'نشر عرض خدمة توصيل'}</span>
                    </button>
                  </div>
                </div>

                {/* Quick Service Cards */}
                <div>
                  <h3 className="text-xs font-bold text-slate-500 dark:text-slate-400 mb-2">الخدمات والوصول السريع:</h3>
                  <div className="grid grid-cols-3 gap-2">
                    <button
                      onClick={() => setCurrentTab(1)}
                      className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-center hover:border-red-400 transition shadow-xs flex flex-col items-center"
                    >
                      <div className="w-9 h-9 rounded-full bg-red-50 dark:bg-red-950/50 text-red-600 flex items-center justify-center mb-1.5">
                        <Store className="w-4 h-4" />
                      </div>
                      <span className="text-xs font-bold text-slate-800 dark:text-slate-100">سوق العروض</span>
                      <span className="text-[10px] text-slate-500">{filteredOffers.length} إعلان نشط</span>
                    </button>

                    <button
                      onClick={() => setCurrentTab(4)}
                      className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-center hover:border-emerald-400 transition shadow-xs flex flex-col items-center"
                    >
                      <div className="w-9 h-9 rounded-full bg-emerald-50 dark:bg-emerald-950/50 text-emerald-600 flex items-center justify-center mb-1.5">
                        <Bike className="w-4 h-4" />
                      </div>
                      <span className="text-xs font-bold text-slate-800 dark:text-slate-100">دليل السائقين</span>
                      <span className="text-[10px] text-slate-500">ليفرورات متاحون</span>
                    </button>

                    <button
                      onClick={() => setCurrentTab(2)}
                      className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-center hover:border-blue-400 transition shadow-xs flex flex-col items-center"
                    >
                      <div className="w-9 h-9 rounded-full bg-blue-50 dark:bg-blue-950/50 text-blue-600 flex items-center justify-center mb-1.5">
                        <MapIcon className="w-4 h-4" />
                      </div>
                      <span className="text-xs font-bold text-slate-800 dark:text-slate-100">خريطة المغرب</span>
                      <span className="text-[10px] text-slate-500">تغطية وطنية</span>
                    </button>
                  </div>
                </div>

                {/* Recent Offers Section */}
                <div>
                  <div className="flex justify-between items-center mb-2">
                    <h3 className="text-xs font-bold text-slate-700 dark:text-slate-200">
                      أحدث الإعلانات في ({selectedCity}):
                    </h3>
                    <button
                      onClick={() => setCurrentTab(1)}
                      className="text-xs font-bold text-red-600 hover:text-red-700"
                    >
                      عرض الكل
                    </button>
                  </div>

                  {filteredOffers.length === 0 ? (
                    <div className="bg-white dark:bg-slate-900 p-6 rounded-xl border border-slate-200 dark:border-slate-800 text-center">
                      <AlertCircle className="w-8 h-8 text-slate-400 mx-auto mb-2" />
                      <p className="text-xs font-bold text-slate-700 dark:text-slate-300">لا توجد إعلانات حالياً في {selectedCity}</p>
                      <button
                        onClick={() => setCurrentTab(3)}
                        className="mt-3 text-xs bg-red-600 text-white font-bold px-3 py-1.5 rounded-lg"
                      >
                        كن أول من ينشر إعلاناً
                      </button>
                    </div>
                  ) : (
                    <div className="space-y-2.5">
                      {filteredOffers.slice(0, 3).map((offer) => (
                        <div
                          key={offer.id}
                          className="p-3.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs"
                        >
                          <div className="flex justify-between items-start gap-2">
                            <span
                              className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                                offer.roleType === 'طلب توصيل'
                                  ? 'bg-red-50 dark:bg-red-950 text-red-600 border border-red-200 dark:border-red-800'
                                  : 'bg-blue-50 dark:bg-blue-950 text-blue-600 border border-blue-200 dark:border-blue-800'
                              }`}
                            >
                              {offer.roleType}
                            </span>
                            <span className="text-[11px] text-slate-400 flex items-center gap-1">
                              <Clock className="w-3 h-3" />
                              {offer.timeAgo}
                            </span>
                          </div>

                          <h4 className="text-xs font-bold text-slate-900 dark:text-white mt-1.5">{offer.title}</h4>
                          <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5 flex items-center gap-1">
                            <MapPin className="w-3 h-3 text-red-500" />
                            {offer.city} • {offer.district}
                          </p>

                          <p className="text-xs text-slate-600 dark:text-slate-300 mt-2 bg-slate-50 dark:bg-slate-800/60 p-2 rounded-lg">
                            {offer.details}
                          </p>

                          <div className="mt-2.5 flex items-center justify-between pt-2 border-t border-slate-100 dark:border-slate-800">
                            <span className="text-xs font-bold text-slate-700 dark:text-slate-300 flex items-center gap-1">
                              <Phone className="w-3 h-3 text-emerald-600" />
                              {offer.phone}
                            </span>
                            <a
                              href={`https://wa.me/212${offer.phone.replace(/^0/, '')}`}
                              target="_blank"
                              rel="noreferrer"
                              className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-lg flex items-center gap-1 shadow-xs transition"
                            >
                              <MessageCircle className="w-3 h-3" />
                              <span>واتساب</span>
                            </a>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* TAB 1: OFFERS */}
            {currentTab === 1 && (
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <div>
                    <h2 className="text-sm font-extrabold text-slate-900 dark:text-white">سوق طلبات وعروض التوصيل</h2>
                    <p className="text-[11px] text-slate-500">
                      المدينة: {selectedCity} ({filteredOffers.length} إعلان)
                    </p>
                  </div>
                  <button
                    onClick={() => setCurrentTab(3)}
                    className="p-2 rounded-xl bg-red-600 text-white hover:bg-red-700 shadow flex items-center gap-1 text-xs font-bold"
                  >
                    <Plus className="w-4 h-4" />
                    <span>إضافة</span>
                  </button>
                </div>

                {filteredOffers.length === 0 ? (
                  <div className="bg-white dark:bg-slate-900 p-8 rounded-xl text-center border border-slate-200 dark:border-slate-800">
                    <AlertCircle className="w-10 h-10 text-slate-400 mx-auto mb-2" />
                    <p className="text-sm font-bold text-slate-700 dark:text-slate-300">لا توجد عروض في {selectedCity}</p>
                    <p className="text-xs text-slate-500 mt-1">اختر مدينة أخرى من القائمة العلوية أو انشر إعلانك الآن مجاناً.</p>
                  </div>
                ) : (
                  <div className="space-y-2.5">
                    {filteredOffers.map((offer) => (
                      <div
                        key={offer.id}
                        className="p-3.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs"
                      >
                        <div className="flex justify-between items-start gap-2">
                          <span
                            className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                              offer.roleType === 'طلب توصيل'
                                ? 'bg-red-50 dark:bg-red-950 text-red-600 border border-red-200 dark:border-red-800'
                                : 'bg-blue-50 dark:bg-blue-950 text-blue-600 border border-blue-200 dark:border-blue-800'
                            }`}
                          >
                            {offer.roleType}
                          </span>
                          <span className="text-[11px] text-slate-400 flex items-center gap-1">
                            <Clock className="w-3 h-3" />
                            {offer.timeAgo}
                          </span>
                        </div>

                        <h4 className="text-xs font-bold text-slate-900 dark:text-white mt-1.5">{offer.title}</h4>
                        <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5 flex items-center gap-1">
                          <MapPin className="w-3 h-3 text-red-500" />
                          {offer.city} • {offer.district}
                        </p>

                        <p className="text-xs text-slate-600 dark:text-slate-300 mt-2 bg-slate-50 dark:bg-slate-800/60 p-2 rounded-lg">
                          {offer.details}
                        </p>

                        <div className="mt-2.5 flex items-center justify-between pt-2 border-t border-slate-100 dark:border-slate-800">
                          <span className="text-xs font-bold text-slate-700 dark:text-slate-300 flex items-center gap-1">
                            <Phone className="w-3 h-3 text-emerald-600" />
                            {offer.phone}
                          </span>
                          <a
                            href={`https://wa.me/212${offer.phone.replace(/^0/, '')}`}
                            target="_blank"
                            rel="noreferrer"
                            className="px-3 py-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-lg flex items-center gap-1 shadow-xs transition"
                          >
                            <MessageCircle className="w-3 h-3" />
                            <span>تواصل عبر واتساب</span>
                          </a>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* TAB 2: MAP */}
            {currentTab === 2 && (
              <div className="space-y-3">
                <div className="bg-white dark:bg-slate-900 p-4 rounded-xl border border-slate-200 dark:border-slate-800 shadow-xs">
                  <div className="flex items-center gap-2">
                    <Navigation className="w-5 h-5 text-red-600" />
                    <div>
                      <h2 className="text-sm font-extrabold text-slate-900 dark:text-white">خريطة التغطية الوطنية بالمغرب</h2>
                      <p className="text-[11px] text-slate-500">LIVLINK تغطي جميع جهات ومدن المملكة المغربية</p>
                    </div>
                  </div>
                </div>

                {/* Simulated Interactive Morocco Map */}
                <div className="bg-slate-900 rounded-2xl p-5 text-white border border-slate-800 relative overflow-hidden flex flex-col items-center justify-center min-h-[300px] text-center">
                  <div className="absolute inset-0 opacity-10 bg-[radial-gradient(#e53935_1px,transparent_1px)] [background-size:16px_16px]"></div>

                  <div className="w-16 h-16 rounded-full bg-red-600/20 border border-red-500/40 flex items-center justify-center mb-3">
                    <MapIcon className="w-8 h-8 text-red-500" />
                  </div>

                  <h3 className="text-base font-black">المملكة المغربية — تغطية وطنية شاملة</h3>
                  <p className="text-xs text-red-400 font-bold mt-1">المدينة المحددة حالياً: {selectedCity}</p>

                  <div className="mt-4 flex flex-wrap justify-center gap-1.5 max-w-sm">
                    {['الدار البيضاء', 'الرباط', 'فاس', 'طنجة', 'مراكش', 'أكادير', 'مكناس', 'وجدة', 'العيون', 'تطوان'].map((city) => (
                      <span
                        key={city}
                        onClick={() => setSelectedCity(city)}
                        className={`cursor-pointer px-2 py-1 rounded text-[11px] font-bold transition ${
                          selectedCity === city
                            ? 'bg-red-600 text-white'
                            : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                        }`}
                      >
                        {city}
                      </span>
                    ))}
                  </div>

                  <div className="mt-5 p-3 rounded-xl bg-slate-800/80 border border-slate-700/60 max-w-xs text-[11px] text-slate-400 leading-relaxed">
                    <span className="font-bold text-slate-200 block mb-0.5">ملاحظة تقنية:</span>
                    التطبيق مهيأ جغرافياً بربط المحلات بالليفرورات حسب المدن والأحياء. تشغيل خريطة Google Maps التفاعلية الفورية يتطلب تفعيل مفتاح Google Maps Platform في نسخة الإنتاج.
                  </div>
                </div>
              </div>
            )}

            {/* TAB 3: ADD */}
            {currentTab === 3 && (
              <div className="bg-white dark:bg-slate-900 rounded-2xl p-4 border border-slate-200 dark:border-slate-800 shadow-sm space-y-4">
                <div className="flex items-center justify-between pb-2 border-b border-slate-100 dark:border-slate-800">
                  <h2 className="text-sm font-extrabold text-slate-900 dark:text-white">
                    {addType === 'طلب توصيل' ? 'إضافة طلب توصيل جديد' : 'إضافة عرض ليفرور جديد'}
                  </h2>
                  <span className="text-[11px] bg-red-100 dark:bg-red-950 text-red-600 px-2 py-0.5 rounded font-bold">
                    مجاني 100٪
                  </span>
                </div>

                {addSuccessMessage ? (
                  <div className="p-6 text-center space-y-2">
                    <CheckCircle className="w-12 h-12 text-emerald-500 mx-auto" />
                    <h3 className="text-sm font-bold text-slate-900 dark:text-white">تم نشر إعلانك بنجاح في LIVLINK!</h3>
                    <p className="text-xs text-slate-500">إعلانك ظاهر الآن في مدينة {addCity} لجميع المستخدمين مجاناً.</p>
                  </div>
                ) : (
                  <form onSubmit={handleAddNewSubmit} className="space-y-3 text-xs">
                    {/* Free platform reassurance */}
                    <div className="p-2.5 rounded-lg bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900 text-red-700 dark:text-red-300 text-[11px]">
                      ⚡ منصة LIVLINK وطنية ومجانية 100٪ — بدون أي عمولة أو اشتراك شهري يفرضه التطبيق.
                    </div>

                    {/* Type Selector */}
                    <div>
                      <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1.5">نوع الإعلان:</label>
                      <div className="grid grid-cols-2 gap-2">
                        <button
                          type="button"
                          onClick={() => setAddType('طلب توصيل')}
                          className={`py-2 px-3 rounded-lg font-bold border transition text-center ${
                            addType === 'طلب توصيل'
                              ? 'bg-red-600 text-white border-red-600 shadow'
                              : 'bg-slate-50 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border-slate-200 dark:border-slate-700'
                          }`}
                        >
                          طلب توصيل (محل / تاجر)
                        </button>
                        <button
                          type="button"
                          onClick={() => setAddType('عرض توصيل')}
                          className={`py-2 px-3 rounded-lg font-bold border transition text-center ${
                            addType === 'عرض توصيل'
                              ? 'bg-slate-900 text-white border-slate-900 shadow'
                              : 'bg-slate-50 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border-slate-200 dark:border-slate-700'
                          }`}
                        >
                          عرض توصيل (ليفرور)
                        </button>
                      </div>
                    </div>

                    {/* Title */}
                    <div>
                      <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1">
                        {addType === 'طلب توصيل' ? 'اسم المحل ونوع السلعة *' : 'اسم الليفرور ووسيلة النقل *'}
                      </label>
                      <input
                        type="text"
                        required
                        value={addTitle}
                        onChange={(e) => setAddTitle(e.target.value)}
                        placeholder={addType === 'طلب توصيل' ? 'مثال: بيتزيريا النصر - توصيل وجبات' : 'مثال: أمين - دراجة نارية للتوصيل'}
                        className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg px-3 py-2 text-slate-800 dark:text-white focus:outline-none focus:ring-1 focus:ring-red-500"
                      />
                    </div>

                    {/* City */}
                    <div>
                      <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1">
                        المدينة (تغطية كاملة للمغرب) *
                      </label>
                      <select
                        value={addCity}
                        onChange={(e) => setAddCity(e.target.value)}
                        className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg px-3 py-2 text-slate-800 dark:text-white focus:outline-none focus:ring-1 focus:ring-red-500"
                      >
                        {MOROCCAN_CITIES.filter((c) => c !== 'جميع مدن المغرب').map((city) => (
                          <option key={city} value={city}>
                            {city}
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Phone */}
                    <div>
                      <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1">
                        رقم الهاتف / الواتساب للتواصل *
                      </label>
                      <input
                        type="tel"
                        required
                        value={addPhone}
                        onChange={(e) => setAddPhone(e.target.value)}
                        placeholder="06XXXXXXXX"
                        className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg px-3 py-2 text-slate-800 dark:text-white focus:outline-none focus:ring-1 focus:ring-red-500 text-left"
                      />
                    </div>

                    {/* Details */}
                    <div>
                      <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1">
                        تفاصيل إضافية (الحي، التوقيت، طبيعة السلع)
                      </label>
                      <textarea
                        rows={3}
                        value={addDetails}
                        onChange={(e) => setAddDetails(e.target.value)}
                        placeholder="اكتب هنا الحي ومواعيد العمل وشروط التوصيل..."
                        className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg px-3 py-2 text-slate-800 dark:text-white focus:outline-none focus:ring-1 focus:ring-red-500"
                      ></textarea>
                    </div>

                    <button
                      type="submit"
                      className="w-full py-2.5 rounded-xl bg-red-600 hover:bg-red-700 text-white font-bold text-xs shadow transition mt-2"
                    >
                      {addType === 'طلب توصيل' ? 'نشر طلب التوصيل مجاناً' : 'نشر عرض الخدمة مجاناً'}
                    </button>
                  </form>
                )}
              </div>
            )}

            {/* TAB 4: DRIVERS */}
            {currentTab === 4 && (
              <div className="space-y-3">
                <div className="bg-white dark:bg-slate-900 p-3.5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-xs flex justify-between items-center">
                  <div>
                    <h2 className="text-sm font-extrabold text-slate-900 dark:text-white">دليل السائقين (الليفرورات)</h2>
                    <p className="text-[11px] text-slate-500">سائقون محترفون متاحون في {selectedCity}</p>
                  </div>
                  <span className="text-xs bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-400 font-bold px-2 py-0.5 rounded">
                    {filteredDrivers.filter((d) => d.isAvailable).length} متاح الآن
                  </span>
                </div>

                {filteredDrivers.length === 0 ? (
                  <div className="bg-white dark:bg-slate-900 p-8 rounded-xl text-center border border-slate-200 dark:border-slate-800">
                    <p className="text-xs text-slate-500">لا يوجد سائقون مسجلون حالياً في {selectedCity}</p>
                  </div>
                ) : (
                  <div className="space-y-2.5">
                    {filteredDrivers.map((driver) => (
                      <div
                        key={driver.id}
                        className="p-3.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs flex items-center justify-between"
                      >
                        <div className="flex items-center gap-3">
                          <div
                            className={`w-10 h-10 rounded-full flex items-center justify-center ${
                              driver.isAvailable
                                ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-300'
                                : 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'
                            }`}
                          >
                            <Bike className="w-5 h-5" />
                          </div>

                          <div>
                            <div className="flex items-center gap-2">
                              <h4 className="text-xs font-bold text-slate-900 dark:text-white">{driver.name}</h4>
                              <span
                                className={`text-[9px] font-bold px-1.5 py-0.2 rounded ${
                                  driver.isAvailable
                                    ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-300'
                                    : 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'
                                }`}
                              >
                                {driver.isAvailable ? 'متاح' : 'مشغول'}
                              </span>
                            </div>

                            <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">
                              {driver.city} • {driver.vehicle} ({driver.experience})
                            </p>
                          </div>
                        </div>

                        <a
                          href={`tel:${driver.phone}`}
                          className="px-3 py-1.5 rounded-lg bg-slate-900 dark:bg-slate-800 hover:bg-slate-800 text-white text-xs font-bold flex items-center gap-1 transition"
                        >
                          <Phone className="w-3.5 h-3.5 text-emerald-400" />
                          <span>اتصال</span>
                        </a>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Bottom Navigation Bar (5 destinations) */}
          <div className="bg-slate-900 border-t border-slate-800 px-2 py-1.5 flex justify-around items-center select-none">
            {[
              { label: 'الرئيسية', icon: Store, index: 0 },
              { label: 'العروض', icon: ListFilter, index: 1 },
              { label: 'الخريطة', icon: MapIcon, index: 2 },
              { label: 'إضافة', icon: Plus, index: 3 },
              { label: 'السائقون', icon: Users, index: 4 }
            ].map(({ label, icon: IconComponent, index }) => (
              <button
                key={label}
                onClick={() => setCurrentTab(index)}
                className={`flex flex-col items-center py-1 px-3 rounded-lg transition ${
                  currentTab === index
                    ? 'text-red-500 font-bold'
                    : 'text-slate-400 hover:text-slate-200 font-medium'
                }`}
              >
                <div
                  className={`p-1 rounded-full ${
                    index === 3
                      ? 'bg-red-600 text-white shadow-md -mt-2'
                      : currentTab === index
                      ? 'bg-slate-800'
                      : ''
                  }`}
                >
                  <IconComponent className="w-4 h-4" />
                </div>
                <span className="text-[10px] mt-0.5">{label}</span>
              </button>
            ))}
          </div>
        </div>
      </main>

      {/* Code Inspector Modal (viewing fixed Kotlin / Compose & Gradle files) */}
      {showCodeInspector && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-3 sm:p-6">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-3xl max-h-[85vh] flex flex-col overflow-hidden text-white shadow-2xl">
            {/* Modal header */}
            <div className="px-4 py-3 border-b border-slate-800 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <FileCode className="w-5 h-5 text-red-500" />
                <h3 className="font-bold text-sm">مستعرض ملفات أندرويد المكتملة والمُصلحة (Kotlin & Gradle)</h3>
              </div>
              <button
                onClick={() => setShowCodeInspector(false)}
                className="w-7 h-7 rounded-lg bg-slate-800 hover:bg-slate-700 flex items-center justify-center text-slate-300"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* File Switcher Tabs */}
            <div className="flex bg-slate-950 px-3 py-2 gap-2 border-b border-slate-800 overflow-x-auto text-xs">
              <button
                onClick={() => setSelectedInspectorFile('NativeHomeScreenNew')}
                className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                  selectedInspectorFile === 'NativeHomeScreenNew' ? 'bg-red-600 text-white' : 'bg-slate-800 text-slate-300'
                }`}
              >
                NativeHomeScreenNew.kt
              </button>
              <button
                onClick={() => setSelectedInspectorFile('AddScreen')}
                className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                  selectedInspectorFile === 'AddScreen' ? 'bg-red-600 text-white' : 'bg-slate-800 text-slate-300'
                }`}
              >
                AddScreen.kt
              </button>
              <button
                onClick={() => setSelectedInspectorFile('buildGradle')}
                className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                  selectedInspectorFile === 'buildGradle' ? 'bg-red-600 text-white' : 'bg-slate-800 text-slate-300'
                }`}
              >
                app/build.gradle.kts
              </button>
              <button
                onClick={() => setSelectedInspectorFile('workflow')}
                className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                  selectedInspectorFile === 'workflow' ? 'bg-red-600 text-white' : 'bg-slate-800 text-slate-300'
                }`}
              >
                .github/workflows/main.yml
              </button>
            </div>

            {/* Code view */}
            <div className="flex-1 p-4 overflow-auto font-mono text-xs bg-slate-950 text-slate-200 select-all leading-relaxed" dir="ltr">
              {selectedInspectorFile === 'NativeHomeScreenNew' && (
                <pre>
{`// app/src/main/java/com/example/NativeHomeScreenNew.kt
// تم إصلاح: CardElevation في Material 3 عبر CardDefaults.cardElevation(...)
// تم إصلاح: مراجع الألوان color -> tint في Icon و containerColor في Cards/Buttons
// تم إصلاح: توافق المعاملات مع AddScreen والـ 5 وجهات للتنقل السفلي

package com.example

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NativeHomeScreenNew(
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
    // 5 Destinations: 0: الرئيسية, 1: العروض, 2: الخريطة, 3: إضافة, 4: السائقون
    var currentTab by remember { mutableIntStateOf(0) }
    var selectedRole by remember { mutableStateOf("صاحب محل") }
    var selectedCity by remember { mutableStateOf("جميع مدن المغرب") }
    // ... تم كتابة الكود كاملاً في ملف المشروع
}`}
                </pre>
              )}

              {selectedInspectorFile === 'AddScreen' && (
                <pre>
{`// app/src/main/java/com/example/AddScreen.kt
// تم إصلاح: توافق معاملات الدالة Composable بدون تعارض
// تم توحيد الهوية: LIVLINK منصة وطنية مجانية 100% بدون عمولات

package com.example

import androidx.compose.runtime.*
import androidx.compose.material3.*

@Composable
fun AddScreen(
    onDismiss: () -> Unit = {},
    initialType: String = "طلب توصيل",
    onAddSubmit: ((title: String, city: String, phone: String, details: String, type: String) -> Unit)? = null
) {
    // يدعم إضافة طلب توصيل (للمحلات) أو عرض توصيل (للسائقين)
    // مع اختيار المدينة ورقم الهاتف والتحقق من الحقول
}`}
                </pre>
              )}

              {selectedInspectorFile === 'buildGradle' && (
                <pre>
{`// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            // مؤمن عبر متغيرات البيئة لبناء GitHub Actions
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            if (keystoreFile != null && file(keystoreFile).exists()) {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("KEY_ALIAS") ?: ""
                keyPassword = System.getenv("KEY_PASSWORD") ?: ""
            }
        }
    }
}`}
                </pre>
              )}

              {selectedInspectorFile === 'workflow' && (
                <pre>
{`# .github/workflows/main.yml
name: Build LIVLINK Android APK

on: [push, pull_request, workflow_dispatch]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      - run: ./gradlew assembleDebug --stacktrace
      - run: ./gradlew assembleRelease --stacktrace
      - uses: actions/upload-artifact@v4
        with:
          name: livlink-apks
          path: app/build/outputs/apk/**/*.apk`}
                </pre>
              )}
            </div>

            {/* Footer with CI/CD info */}
            <div className="p-3 bg-slate-900 border-t border-slate-800 text-xs text-slate-400 flex items-center justify-between">
              <span>جميع الملفات محفوظة في مسارات المشروع الفعلية وجاهزة للبناء التلقائي عبر GitHub Actions</span>
              <button
                onClick={() => setShowCodeInspector(false)}
                className="px-3 py-1 bg-red-600 text-white rounded-lg font-bold"
              >
                إغلاق
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
