/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import {
  Store,
  Phone,
  MessageCircle,
  MapPin,
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
  User,
  ArrowRightLeft,
  Navigation
} from 'lucide-react';

// Dedicated Vector Icon for Livreur: Delivery Driver on Motorcycle with Delivery Box
export function DeliveryMotorcycleBoxIcon({
  className = 'w-4 h-4',
  color = 'currentColor'
}: {
  className?: string;
  color?: string;
}) {
  return (
    <svg
      viewBox="0 0 24 24"
      fill={color}
      className={className}
      xmlns="http://www.w3.org/2000/svg"
    >
      {/* Insulated Delivery Box mounted on rear rack behind driver */}
      <path d="M2 6h5.8c0.45 0 0.8 0.35 0.8 0.8v4.6c0 0.45-0.35 0.8-0.8 0.8H2c-0.45 0-0.8-0.35-0.8-0.8V6.8c0-0.45 0.35-0.8 0.8-0.8z M2.6 8.2h4.6v1.2H2.6z" />
      {/* Driver Head & Safety Helmet */}
      <path d="M13.2 2.4c1.16 0 2.1 0.94 2.1 2.1s-0.94 2.1-2.1 2.1s-2.1-0.94-2.1-2.1s0.94-2.1 2.1-2.1z" />
      {/* Courier Riding Silhouette & Motorcycle Chassis */}
      <path d="M11.8 7.2l2.7 2.6l3-0.8l-0.3-1.2l-2.4 0.6l-2-1.9z M8 12.2h4.8l2 2.8h-2.2l-1.4-1.8H8z M16.8 8l2.6 8.2h-1.6l-2.2-7.2z M4 12.2l1.5 4.3h1.4l-1.7-4.3z" />
      {/* Rear Wheel with Hub Cutout */}
      <path
        fillRule="evenodd"
        d="M5.5 14.5c1.66 0 3 1.34 3 3s-1.34 3-3 3s-3-1.34-3-3s1.34-3 3-3zm0 1.6c-0.77 0-1.4 0.63-1.4 1.4s0.63 1.4 1.4 1.4s1.4-0.63 1.4-1.4s-0.63-1.4-1.4-1.4z"
      />
      {/* Front Wheel with Hub Cutout */}
      <path
        fillRule="evenodd"
        d="M18.5 14.5c1.66 0 3 1.34 3 3s-1.34 3-3 3s-3-1.34-3-3s1.34-3 3-3zm0 1.6c-0.77 0-1.4 0.63-1.4 1.4s0.63 1.4 1.4 1.4s1.4-0.63 1.4-1.4s-0.63-1.4-1.4-1.4z"
      />
    </svg>
  );
}

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
  completedTrips: number;
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
  // Bottom navigation tabs (strictly in order): 0: الرئيسية, 1: العروض, 2: إضافة, 3: الليفرورات, 4: حسابي
  const [currentTab, setCurrentTab] = useState<number>(0);
  const [selectedRole, setSelectedRole] = useState<'صاحب محل' | 'ليفرور'>('صاحب محل');
  const [selectedCity, setSelectedCity] = useState<string>('جميع مدن المغرب');
  const [isPhoneFrame, setIsPhoneFrame] = useState(true);
  const [showCodeInspector, setShowCodeInspector] = useState(false);
  const [selectedInspectorFile, setSelectedInspectorFile] = useState<'NativeHomeScreenNew' | 'AddScreen' | 'Theme' | 'MainActivity'>('NativeHomeScreenNew');

  const [isDriverAvailable, setIsDriverAvailable] = useState(true);

  // Load saved role from localStorage
  useEffect(() => {
    const saved = localStorage.getItem('livlink_selected_role');
    if (saved === 'ليفرور' || saved === 'صاحب محل') {
      setSelectedRole(saved);
    }
  }, []);

  const handleRoleChange = (role: 'صاحب محل' | 'ليفرور') => {
    setSelectedRole(role);
    localStorage.setItem('livlink_selected_role', role);
  };

  const isDriver = selectedRole === 'ليفرور';
  // Centralized Mode Theme Colors: Darker Blue for Shop Owner, Orange for Delivery Driver
  const activeColor = isDriver ? '#EA580C' : '#1E40AF';
  const activeBgLight = isDriver ? 'bg-orange-50' : 'bg-blue-50';
  const activeBorder = isDriver ? 'border-orange-200' : 'border-blue-200';
  const activeText = isDriver ? 'text-orange-600' : 'text-blue-700';

  // Add Screen Form State
  const [addType, setAddType] = useState<'طلب توصيل' | 'عرض توصيل'>(isDriver ? 'عرض توصيل' : 'طلب توصيل');
  const [addTitle, setAddTitle] = useState('');
  const [addCity, setAddCity] = useState('الدار البيضاء');
  const [addPhone, setAddPhone] = useState('');
  const [addDetails, setAddDetails] = useState('');
  const [addSuccessMessage, setAddSuccessMessage] = useState(false);

  // Sync addType with role changes
  useEffect(() => {
    setAddType(isDriver ? 'عرض توصيل' : 'طلب توصيل');
  }, [isDriver]);

  // Offers Data
  const [offers, setOffers] = useState<DeliveryOffer[]>([
    {
      id: '1',
      title: 'محل إلكترونيات وأكسسوارات الهواتف',
      city: 'الدار البيضاء',
      district: 'المعاريف / سيدي معروف',
      roleType: 'طلب توصيل',
      phone: '0661001122',
      timeAgo: 'منذ 10 دقائق',
      details: 'مطلوب ليفرور لتوصيل طلبيات يومية ثابتة ومضمونة من 10 صباحاً إلى 7 مساءً.'
    },
    {
      id: '2',
      title: 'ليفرور بموتور خاص (توصيل سريع مع صندوق)',
      city: 'الرباط',
      district: 'أكدال / حسان / السويسي',
      roleType: 'عرض توصيل',
      phone: '0670334455',
      timeAgo: 'منذ 25 دقيقة',
      details: 'سائق دراجة نارية سريع وموثوق متاح لتغطية طلبات المطاعم والمحلات بمدينة الرباط وسلا ونواحيها.',
      vehicleType: 'دراجة نارية مع صندوق'
    },
    {
      id: '3',
      title: 'مطعم وسناك مأكولات سريعة',
      city: 'فاس',
      district: 'فاس الجديد / النرجس',
      roleType: 'طلب توصيل',
      phone: '0652889900',
      timeAgo: 'منذ 45 دقيقة',
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
  const [drivers, setDrivers] = useState<DriverProfile[]>([
    { id: 'd1', name: 'يوسف العمراني', city: 'الدار البيضاء', vehicle: 'دراجة نارية مع صندوق', phone: '0661122334', isAvailable: true, experience: '3 سنوات', completedTrips: 420 },
    { id: 'd2', name: 'كريم السبتي', city: 'الرباط', vehicle: 'دراجة نارية مع صندوق', phone: '0672233445', isAvailable: true, experience: 'سنتان', completedTrips: 280 },
    { id: 'd3', name: 'حمزة التازي', city: 'فاس', vehicle: 'دراجة نارية', phone: '0653344556', isAvailable: true, experience: '4 سنوات', completedTrips: 510 },
    { id: 'd4', name: 'أنس البورقادي', city: 'طنجة', vehicle: 'سيارة خفيفة', phone: '0664455667', isAvailable: false, experience: '5 سنوات', completedTrips: 630 },
    { id: 'd5', name: 'رشيد المرابط', city: 'مراكش', vehicle: 'دراجة نارية مع صندوق', phone: '0675566778', isAvailable: true, experience: 'سنة واحدة', completedTrips: 150 },
    { id: 'd6', name: 'طارق بنعلي', city: 'أكادير', vehicle: 'دراجة نارية', phone: '0656677889', isAvailable: true, experience: 'سنتان', completedTrips: 310 }
  ]);

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
    }, 1400);
  };

  return (
    <div className={`min-h-screen ${isDarkMode ? 'bg-slate-950 text-slate-100' : 'bg-slate-100 text-slate-900'} font-sans flex flex-col`}>
      {/* Top AI Studio Header Bar */}
      <header className="bg-slate-900 border-b border-slate-800 px-4 py-2 flex items-center justify-between text-white text-xs select-none shadow-sm">
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
          <span className="font-bold tracking-wide text-sm bg-gradient-to-r from-blue-400 to-indigo-400 bg-clip-text text-transparent">
            LIVLINK MA
          </span>
          <span className="text-slate-400 hidden sm:inline">| المرحلة الأولى — إصلاح المساحة والتنقل والأوضاع</span>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => setIsPhoneFrame(!isPhoneFrame)}
            className="flex items-center gap-1.5 px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-200 transition"
          >
            {isPhoneFrame ? <Maximize2 className="w-3.5 h-3.5" /> : <Smartphone className="w-3.5 h-3.5" />}
            <span className="hidden md:inline">{isPhoneFrame ? 'ملء الشاشة' : 'إطار الهاتف'}</span>
          </button>

          <button
            onClick={() => setShowCodeInspector(true)}
            className={`flex items-center gap-1.5 px-2.5 py-1 rounded text-white font-medium transition ${
              isDriver ? 'bg-orange-600 hover:bg-orange-700' : 'bg-blue-600 hover:bg-blue-700'
            }`}
          >
            <FileCode className="w-3.5 h-3.5" />
            <span>أكواد Kotlin المُصلحة</span>
          </button>
        </div>
      </header>

      {/* Main Container */}
      <main className="flex-1 flex items-center justify-center p-2 sm:p-4 md:p-6 overflow-auto">
        <div
          className={`w-full transition-all duration-300 ${
            isPhoneFrame
              ? 'max-w-[420px] rounded-[40px] shadow-2xl border-[10px] border-slate-900 overflow-hidden ring-1 ring-slate-800'
              : 'max-w-4xl rounded-2xl shadow-xl border border-slate-300 dark:border-slate-800 overflow-hidden'
          } ${isDarkMode ? 'bg-slate-900' : 'bg-white'} flex flex-col h-[780px] max-h-[92vh]`}
        >
          {/* System Status Bar (Safe Area / Inset respected) */}
          <div className="bg-slate-950 text-white px-6 pt-2.5 pb-1 flex justify-between items-center text-[11px] font-medium tracking-tight select-none border-b border-slate-900">
            <span>12:45</span>
            <div className="w-20 h-4 bg-slate-900 rounded-full mx-auto -mt-1 hidden sm:block"></div>
            <div className="flex items-center gap-1.5">
              <span>5G</span>
              <span>100%</span>
            </div>
          </div>

          {/* LIVLINK Top Bar */}
          <div className="bg-slate-900 text-white px-4 py-2.5 border-b border-slate-800">
            {/* Top row: Brand & Reorganized Top Icons */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div
                  className="w-9 h-9 rounded-lg flex items-center justify-center font-black text-white text-base shadow-md transition-colors"
                  style={{ backgroundColor: activeColor }}
                >
                  LV
                </div>
                <div>
                  <h1 className="text-base font-black tracking-wide text-white leading-tight">LIVLINK</h1>
                  <p className="text-[10.5px] text-slate-300 font-medium">منصة ربط المحلات بالليفرورات</p>
                </div>
              </div>

              {/* Refined Top Icons: 1. Store Icon, 2. Delivery Motorcycle with Box, 3. Dark/Light Mode */}
              <div className="flex items-center gap-1.5">
                {/* 1. Store Icon (Switches to Shop Owner mode) */}
                <button
                  onClick={() => handleRoleChange('صاحب محل')}
                  title="وضع صاحب المحل"
                  className={`w-8 h-8 rounded-full flex items-center justify-center transition border ${
                    !isDriver
                      ? 'bg-[#1E40AF] text-white border-blue-400 shadow-sm ring-1 ring-blue-700/40'
                      : 'bg-slate-800 text-slate-400 border-slate-700 hover:text-white'
                  }`}
                >
                  <Store className="w-4 h-4" />
                </button>

                {/* 2. Delivery Motorcycle with Box (Switches to Driver mode) */}
                <button
                  onClick={() => handleRoleChange('ليفرور')}
                  title="وضع سائق التوصيل (ليفرور)"
                  className={`w-8 h-8 rounded-full flex items-center justify-center transition border ${
                    isDriver
                      ? 'bg-orange-600 text-white border-orange-400 shadow-sm ring-1 ring-orange-500/40'
                      : 'bg-slate-800 text-slate-400 border-slate-700 hover:text-white'
                  }`}
                >
                  <DeliveryMotorcycleBoxIcon className="w-4 h-4" />
                </button>

                {/* 3. Dark Mode Toggle */}
                <button
                  onClick={() => setIsDarkMode(!isDarkMode)}
                  title="تبديل الوضع الليلي / النهاري"
                  className="w-8 h-8 rounded-full bg-slate-800 hover:bg-slate-700 border border-slate-700 flex items-center justify-center text-slate-300 transition"
                >
                  {isDarkMode ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* Corrected Official Banner: Exact phrase, no repeated tag */}
            <div className="mt-2 bg-slate-800/90 rounded-lg px-2.5 py-1.5 flex items-center gap-2 text-[11px] text-slate-200 border border-slate-700/50">
              <ShieldCheck className="w-4 h-4 shrink-0" style={{ color: activeColor }} />
              <span className="font-medium">منصة وطنية مغربية مجانية 100% تغطى جميع مدن المملكة</span>
            </div>

            {/* Role Indicator & City Selector Row */}
            <div className="mt-2 grid grid-cols-2 gap-2">
              <button
                onClick={() => handleRoleChange(isDriver ? 'صاحب محل' : 'ليفرور')}
                className={`py-1.5 px-2 rounded-lg text-xs font-bold transition flex items-center justify-center gap-1.5 border ${
                  isDriver
                    ? 'bg-orange-950/60 border-orange-600/50 text-orange-300'
                    : 'bg-blue-950/60 border-blue-800 text-blue-300'
                }`}
              >
                {isDriver ? <DeliveryMotorcycleBoxIcon className="w-3.5 h-3.5" /> : <Store className="w-3.5 h-3.5" />}
                <span>{isDriver ? 'وضع الليفرور (نشط)' : 'وضع صاحب المحل (نشط)'}</span>
              </button>

              {/* City Dropdown */}
              <div className="relative">
                <select
                  value={selectedCity}
                  onChange={(e) => setSelectedCity(e.target.value)}
                  className="w-full bg-slate-800 text-white text-xs font-medium rounded-lg px-2 py-1.5 pr-2 appearance-none border border-slate-700 focus:outline-none cursor-pointer"
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

          {/* App Body Content */}
          <div className={`flex-1 overflow-y-auto p-3.5 ${isDarkMode ? 'bg-[#0A0F1D]' : 'bg-slate-100/90'}`}>
            {/* TAB 0: HOME */}
            {currentTab === 0 && (
              <div className="space-y-3 pb-8">
                {/* Hero Role Card */}
                <div
                  className={`rounded-2xl p-4 text-white shadow-md relative overflow-hidden transition-all ${
                    isDriver
                      ? 'bg-gradient-to-br from-orange-600 to-amber-700'
                      : 'bg-gradient-to-br from-blue-600 to-indigo-700'
                  }`}
                >
                  <div className="flex justify-between items-start">
                    <span className="text-xs font-bold bg-white/20 px-2 py-0.5 rounded-full backdrop-blur-sm">
                      {isDriver ? 'لوحة تحكم الليفرور' : 'لوحة تحكم صاحب المحل'}
                    </span>
                    <span className="text-[10px] bg-slate-900/40 text-white font-bold px-2 py-0.5 rounded border border-white/20">
                      {selectedCity}
                    </span>
                  </div>

                  <h2 className="text-base font-extrabold mt-2">
                    {isDriver ? 'أهلاً بك يا كابتن التوصيل!' : 'مرحباً بك يا صاحب المحل!'}
                  </h2>

                  <p className="text-xs mt-1 text-slate-100 leading-relaxed opacity-95">
                    {isDriver
                      ? `استعرض طلبيات المحلات والمطاعم المتاحة بمدينة ${selectedCity} وتواصل فوراً مع أصحابها لبدء التوصيل.`
                      : `انشر طلبيات التوصيل الخاصة بمحلك وتواصل مباشرة مع ليفرورات ${selectedCity} بدون أي وسيط وبدون اقتطاع أي عمولة.`}
                  </p>

                  <div className="mt-3.5 flex gap-2">
                    <button
                      onClick={() => setCurrentTab(2)}
                      className="flex-1 py-2 px-3 rounded-xl font-bold text-xs flex items-center justify-center gap-1.5 shadow transition bg-slate-950 hover:bg-slate-900 text-white"
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>{isDriver ? 'نشر عرض جاهزية للتوصيل' : 'إضافة طلب توصيل جديد لمحلك'}</span>
                    </button>
                  </div>
                </div>

                {/* Driver Availability Toggle (Driver Mode Only) */}
                {isDriver && (
                  <div className="p-3 rounded-xl border border-orange-200 dark:border-orange-950/60 bg-orange-50 dark:bg-orange-950/20 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <div className={`w-2.5 h-2.5 rounded-full ${isDriverAvailable ? 'bg-emerald-500 animate-pulse' : 'bg-slate-400'}`}></div>
                      <div>
                        <p className="text-xs font-bold text-slate-800 dark:text-slate-100">
                          {isDriverAvailable ? 'حالتك: متاح حالياً للتوصيل' : 'حالتك: غير متاح حالياً'}
                        </p>
                        <p className="text-[10px] text-slate-500">
                          {isDriverAvailable ? 'المحلات في مدينتك يمكنها الاتصال بك الآن' : 'تم إيقاف ظهور إعلانك مؤقتاً'}
                        </p>
                      </div>
                    </div>
                    <button
                      onClick={() => setIsDriverAvailable(!isDriverAvailable)}
                      className={`px-3 py-1 rounded-lg text-xs font-bold text-white transition ${
                        isDriverAvailable ? 'bg-emerald-600 hover:bg-emerald-700' : 'bg-slate-600 hover:bg-slate-700'
                      }`}
                    >
                      {isDriverAvailable ? 'نشط' : 'متوقف'}
                    </button>
                  </div>
                )}

                {/* Quick Access Services */}
                <div>
                  <h3 className="text-xs font-bold text-slate-500 dark:text-slate-400 mb-2">الخدمات والوصول السريع:</h3>
                  <div className="grid grid-cols-3 gap-2">
                    <button
                      onClick={() => setCurrentTab(1)}
                      className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-center hover:border-slate-300 transition shadow-xs flex flex-col items-center"
                    >
                      <div
                        className="w-9 h-9 rounded-full flex items-center justify-center mb-1.5"
                        style={{ backgroundColor: `${activeColor}15`, color: activeColor }}
                      >
                        <ListFilter className="w-4 h-4" />
                      </div>
                      <span className="text-xs font-bold text-slate-800 dark:text-slate-100">
                        {isDriver ? 'طلبيات المحلات' : 'سوق العروض'}
                      </span>
                      <span className="text-[10px] text-slate-500">{filteredOffers.length} إعلان</span>
                    </button>

                    <button
                      onClick={() => setCurrentTab(2)}
                      className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-center hover:border-slate-300 transition shadow-xs flex flex-col items-center"
                    >
                      <div
                        className="w-9 h-9 rounded-full flex items-center justify-center mb-1.5"
                        style={{ backgroundColor: `${activeColor}15`, color: activeColor }}
                      >
                        <Plus className="w-4 h-4" />
                      </div>
                      <span className="text-xs font-bold text-slate-800 dark:text-slate-100">
                        {isDriver ? 'نشر جاهزية' : 'طلب ليفرور'}
                      </span>
                      <span className="text-[10px] text-slate-500">نشر مجاني</span>
                    </button>

                    <button
                      onClick={() => setCurrentTab(3)}
                      className="p-3 rounded-xl bg-white dark:bg-[#162032] border border-slate-200 dark:border-slate-700/60 text-center hover:border-slate-300 transition shadow-xs flex flex-col items-center"
                    >
                      <div className="w-9 h-9 rounded-full bg-emerald-50 dark:bg-emerald-950/50 text-emerald-600 flex items-center justify-center mb-1.5">
                        <DeliveryMotorcycleBoxIcon className="w-4 h-4" />
                      </div>
                      <span className="text-xs font-bold text-slate-800 dark:text-slate-100">الليفرورات</span>
                      <span className="text-[10px] text-slate-500">سائقون معتمدون</span>
                    </button>
                  </div>
                </div>

                {/* Recent Offers Section */}
                <div>
                  <div className="flex justify-between items-center mb-2">
                    <h3 className="text-xs font-bold text-slate-700 dark:text-slate-200">
                      {isDriver ? `أحدث طلبات التوصيل المطلوبة في (${selectedCity}):` : `أحدث الإعلانات في (${selectedCity}):`}
                    </h3>
                    <button onClick={() => setCurrentTab(1)} className="text-xs font-bold hover:underline" style={{ color: activeColor }}>
                      عرض الكل
                    </button>
                  </div>

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
                                ? 'bg-blue-50 dark:bg-blue-950 text-blue-600 border border-blue-200 dark:border-blue-800'
                                : 'bg-orange-50 dark:bg-orange-950 text-orange-600 border border-orange-200 dark:border-orange-800'
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
                          <div className="flex gap-1.5">
                            <a
                              href={`https://wa.me/212${offer.phone.replace(/^0/, '')}`}
                              target="_blank"
                              rel="noreferrer"
                              className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-lg flex items-center gap-1 shadow-xs transition"
                            >
                              <MessageCircle className="w-3 h-3" />
                              <span>واتساب</span>
                            </a>
                            <a
                              href={`tel:${offer.phone}`}
                              className="px-2.5 py-1 bg-slate-900 dark:bg-slate-800 hover:bg-slate-800 text-white text-xs font-bold rounded-lg flex items-center gap-1 shadow-xs transition"
                            >
                              <Phone className="w-3 h-3" />
                              <span>اتصال</span>
                            </a>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* TAB 1: OFFERS */}
            {currentTab === 1 && (
              <div className="space-y-3 pb-8">
                <div className="flex items-center justify-between">
                  <div>
                    <h2 className="text-sm font-extrabold text-slate-900 dark:text-white">
                      {isDriver ? 'طلبيات المحلات المتاحة للتوصيل' : 'سوق العروض وطلبات التوصيل'}
                    </h2>
                    <p className="text-[11px] text-slate-500">
                      المدينة: {selectedCity} ({filteredOffers.length} إعلان)
                    </p>
                  </div>
                  <button
                    onClick={() => setCurrentTab(2)}
                    className="p-2 rounded-xl text-white shadow flex items-center gap-1 text-xs font-bold transition"
                    style={{ backgroundColor: activeColor }}
                  >
                    <Plus className="w-4 h-4" />
                    <span>إضافة</span>
                  </button>
                </div>

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
                              ? 'bg-blue-50 dark:bg-blue-950 text-blue-600 border border-blue-200 dark:border-blue-800'
                              : 'bg-orange-50 dark:bg-orange-950 text-orange-600 border border-orange-200 dark:border-orange-800'
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
                        <div className="flex gap-1.5">
                          <a
                            href={`https://wa.me/212${offer.phone.replace(/^0/, '')}`}
                            target="_blank"
                            rel="noreferrer"
                            className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-lg flex items-center gap-1 shadow-xs transition"
                          >
                            <MessageCircle className="w-3 h-3" />
                            <span>واتساب</span>
                          </a>
                          <a
                            href={`tel:${offer.phone}`}
                            className="px-2.5 py-1 bg-slate-900 dark:bg-slate-800 hover:bg-slate-800 text-white text-xs font-bold rounded-lg flex items-center gap-1 shadow-xs transition"
                          >
                            <Phone className="w-3 h-3" />
                            <span>اتصال</span>
                          </a>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* TAB 2: ADD (CENTER TAB) */}
            {currentTab === 2 && (
              <div className="bg-white dark:bg-slate-900 rounded-2xl p-4 border border-slate-200 dark:border-slate-800 shadow-sm space-y-4 pb-8">
                <div className="flex items-center justify-between pb-2 border-b border-slate-100 dark:border-slate-800">
                  <h2 className="text-sm font-extrabold text-slate-900 dark:text-white">
                    {addType === 'طلب توصيل' ? 'إضافة طلب توصيل جديد' : 'إضافة عرض ليفرور جديد'}
                  </h2>
                  <span
                    className="text-[11px] px-2 py-0.5 rounded font-bold"
                    style={{ backgroundColor: `${activeColor}20`, color: activeColor }}
                  >
                    {isDriver ? 'وضع الليفرور' : 'وضع المحل'}
                  </span>
                </div>

                {addSuccessMessage ? (
                  <div className="p-6 text-center space-y-2">
                    <CheckCircle className="w-12 h-12 text-emerald-500 mx-auto" />
                    <h3 className="text-sm font-bold text-slate-900 dark:text-white">تم نشر إعلانك بنجاح في LIVLINK!</h3>
                    <p className="text-xs text-slate-500">إعلانك ظاهر الآن في مدينة {addCity} بنجاح.</p>
                  </div>
                ) : (
                  <form onSubmit={handleAddNewSubmit} className="space-y-3 text-xs">
                    {/* Type Selector */}
                    <div>
                      <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1.5">نوع الإعلان:</label>
                      <div className="grid grid-cols-2 gap-2">
                        <button
                          type="button"
                          onClick={() => setAddType('طلب توصيل')}
                          className={`py-2 px-3 rounded-lg font-bold border transition text-center ${
                            addType === 'طلب توصيل'
                              ? 'bg-blue-600 text-white border-blue-600 shadow'
                              : 'bg-slate-50 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border-slate-200 dark:border-slate-700'
                          }`}
                        >
                          طلب توصيل (محل)
                        </button>
                        <button
                          type="button"
                          onClick={() => setAddType('عرض توصيل')}
                          className={`py-2 px-3 rounded-lg font-bold border transition text-center ${
                            addType === 'عرض توصيل'
                              ? 'bg-orange-600 text-white border-orange-600 shadow'
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
                        placeholder={addType === 'طلب توصيل' ? 'مثال: بيتزيريا النصر - وجبات سريعة' : 'مثال: أمين - دراجة نارية للتوصيل السريع'}
                        className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg px-3 py-2 text-slate-800 dark:text-white focus:outline-none"
                      />
                    </div>

                    {/* City */}
                    <div>
                      <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1">
                        المدينة (تغطية وطنية شاملة) *
                      </label>
                      <select
                        value={addCity}
                        onChange={(e) => setAddCity(e.target.value)}
                        className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg px-3 py-2 text-slate-800 dark:text-white focus:outline-none"
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
                        className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg px-3 py-2 text-slate-800 dark:text-white focus:outline-none text-left"
                      />
                    </div>

                    {/* Details */}
                    <div>
                      <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1">
                        تفاصيل إضافية (الأحياء، التوقيت، شروط التوصيل)
                      </label>
                      <textarea
                        rows={3}
                        value={addDetails}
                        onChange={(e) => setAddDetails(e.target.value)}
                        placeholder="اكتب هنا الحي ومواعيد العمل وشروط التوصيل..."
                        className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg px-3 py-2 text-slate-800 dark:text-white focus:outline-none"
                      ></textarea>
                    </div>

                    <button
                      type="submit"
                      className="w-full py-2.5 rounded-xl text-white font-bold text-xs shadow transition mt-2"
                      style={{ backgroundColor: activeColor }}
                    >
                      {addType === 'طلب توصيل' ? 'نشر طلب التوصيل' : 'نشر عرض الخدمة'}
                    </button>
                  </form>
                )}
              </div>
            )}

            {/* TAB 3: DRIVERS (الليفرورات) */}
            {currentTab === 3 && (
              <div className="space-y-3 pb-8">
                <div className="bg-white dark:bg-slate-900 p-3.5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-xs flex justify-between items-center">
                  <div>
                    <h2 className="text-sm font-extrabold text-slate-900 dark:text-white">دليل الليفرورات المعتمدين</h2>
                    <p className="text-[11px] text-slate-500">سائقون محترفون متاحون في {selectedCity}</p>
                  </div>
                  <span className="text-xs bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-400 font-bold px-2 py-0.5 rounded">
                    {filteredDrivers.filter((d) => d.isAvailable).length} متاح الآن
                  </span>
                </div>

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
                          <DeliveryMotorcycleBoxIcon className="w-5 h-5" />
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
                          <p className="text-[10.5px] font-semibold text-emerald-600">
                            {driver.completedTrips} توصيلة ناجحة
                          </p>
                        </div>
                      </div>

                      <div className="flex gap-1.5">
                        <a
                          href={`https://wa.me/212${driver.phone.replace(/^0/, '')}`}
                          target="_blank"
                          rel="noreferrer"
                          className="w-8 h-8 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white flex items-center justify-center transition"
                        >
                          <MessageCircle className="w-4 h-4" />
                        </a>
                        <a
                          href={`tel:${driver.phone}`}
                          className="w-8 h-8 rounded-lg bg-slate-900 dark:bg-slate-800 hover:bg-slate-800 text-white flex items-center justify-center transition"
                        >
                          <Phone className="w-4 h-4" />
                        </a>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* TAB 4: MY ACCOUNT (حسابي) */}
            {currentTab === 4 && (
              <div className="space-y-3 pb-8">
                {/* Profile Header */}
                <div className="bg-white dark:bg-slate-900 rounded-2xl p-4 border border-slate-200 dark:border-slate-800 shadow-sm text-center">
                  <div
                    className="w-16 h-16 rounded-full mx-auto flex items-center justify-center mb-2 border-2"
                    style={{ backgroundColor: `${activeColor}15`, borderColor: activeColor, color: activeColor }}
                  >
                    {isDriver ? <DeliveryMotorcycleBoxIcon className="w-8 h-8" /> : <Store className="w-8 h-8" />}
                  </div>

                  <h3 className="text-sm font-bold text-slate-900 dark:text-white">
                    {isDriver ? 'سائق توصيل معتمد (ليفرور)' : 'محل تجاري معتمد (تاجر)'}
                  </h3>
                  <p className="text-xs text-slate-500 mt-0.5">
                    الوضع النشط: {selectedRole} • {selectedCity}
                  </p>

                  <div className="mt-3 flex justify-center">
                    <button
                      onClick={() => handleRoleChange(isDriver ? 'صاحب محل' : 'ليفرور')}
                      className="px-3 py-1.5 rounded-lg border text-xs font-bold flex items-center gap-1.5 transition"
                      style={{ borderColor: activeColor, color: activeColor }}
                    >
                      <ArrowRightLeft className="w-3.5 h-3.5" />
                      <span>{isDriver ? 'التبديل إلى وضع صاحب المحل' : 'التبديل إلى وضع الليفرور'}</span>
                    </button>
                  </div>
                </div>

                {/* Account Details */}
                <div className="bg-white dark:bg-slate-900 rounded-2xl p-4 border border-slate-200 dark:border-slate-800 shadow-sm space-y-2.5 text-xs">
                  <h4 className="font-bold text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-1.5">
                    معلومات الحساب والتطبيق:
                  </h4>

                  <div className="flex justify-between py-1">
                    <span className="text-slate-500">نوع الحساب:</span>
                    <span className="font-bold text-slate-800 dark:text-slate-200">{selectedRole}</span>
                  </div>

                  <div className="flex justify-between py-1 border-t border-slate-100 dark:border-slate-800">
                    <span className="text-slate-500">المدينة المعتمدة:</span>
                    <span className="font-bold text-slate-800 dark:text-slate-200">{selectedCity}</span>
                  </div>

                  <div className="flex justify-between py-1 border-t border-slate-100 dark:border-slate-800">
                    <span className="text-slate-500">رقم الهاتف الافتراضي:</span>
                    <span className="font-bold text-slate-800 dark:text-slate-200">0661234567</span>
                  </div>

                  {isDriver && (
                    <div className="flex justify-between py-1 border-t border-slate-100 dark:border-slate-800 items-center">
                      <span className="text-slate-500">جاهزية التوصيل:</span>
                      <button
                        onClick={() => setIsDriverAvailable(!isDriverAvailable)}
                        className={`px-2 py-0.5 rounded text-[11px] font-bold text-white ${
                          isDriverAvailable ? 'bg-emerald-600' : 'bg-slate-600'
                        }`}
                      >
                        {isDriverAvailable ? 'متاح' : 'متوقف'}
                      </button>
                    </div>
                  )}

                  <div className="flex justify-between py-1 border-t border-slate-100 dark:border-slate-800">
                    <span className="text-slate-500">نموذج الاستخدام:</span>
                    <span className="font-bold text-emerald-600">مجاني 100٪ بدون عمولات</span>
                  </div>
                </div>

                {/* Notice */}
                <div className="p-3 rounded-xl bg-slate-100 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700/60 text-[11px] text-slate-500 leading-relaxed">
                  <span className="font-bold text-slate-700 dark:text-slate-300 block mb-0.5">توضيح تقني:</span>
                  يتم حفظ إعدادات الدور والتفضيلات محلياً في هذا الإصدار عبر SharedPreferences. الربط السحابي مع قاعدة البيانات الخارجية سيتم إدراجه لاحقاً.
                </div>
              </div>
            )}
          </div>

          {/* Bottom Navigation Bar (Strictly 5 items: 1.الرئيسية, 2.العروض, 3.إضافة, 4.الليفرورات, 5.حسابي) */}
          <div className="bg-slate-900 border-t border-slate-800 px-1 py-1.5 flex justify-around items-center select-none pb-3">
            {[
              { label: 'الرئيسية', icon: Store, index: 0 },
              { label: 'العروض', icon: ListFilter, index: 1 },
              { label: 'إضافة', icon: Plus, index: 2 },
              { label: 'الليفرورات', icon: DeliveryMotorcycleBoxIcon, index: 3 },
              { label: 'حسابي', icon: User, index: 4 }
            ].map(({ label, icon: IconComponent, index }) => {
              const isSelected = currentTab === index;
              return (
                <button
                  key={label}
                  onClick={() => setCurrentTab(index)}
                  className={`flex flex-col items-center py-1 px-2.5 rounded-lg transition ${
                    isSelected ? 'font-bold' : 'text-slate-400 hover:text-slate-200 font-medium'
                  }`}
                  style={{ color: isSelected ? activeColor : undefined }}
                >
                  <div
                    className={`p-1 rounded-full transition-all ${
                      index === 2
                        ? 'text-white shadow-md -mt-2.5'
                        : isSelected
                        ? 'bg-slate-800'
                        : ''
                    }`}
                    style={{ backgroundColor: index === 2 ? activeColor : undefined }}
                  >
                    <IconComponent className="w-4 h-4" />
                  </div>
                  <span className="text-[10px] mt-0.5">{label}</span>
                </button>
              );
            })}
          </div>
        </div>
      </main>

      {/* Code Inspector Modal */}
      {showCodeInspector && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-3 sm:p-6">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-3xl max-h-[85vh] flex flex-col overflow-hidden text-white shadow-2xl">
            <div className="px-4 py-3 border-b border-slate-800 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <FileCode className="w-5 h-5 text-blue-500" />
                <h3 className="font-bold text-sm">مستعرض ملفات Kotlin المعدلة للمرحلة الأولى</h3>
              </div>
              <button
                onClick={() => setShowCodeInspector(false)}
                className="w-7 h-7 rounded-lg bg-slate-800 hover:bg-slate-700 flex items-center justify-center text-slate-300"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="flex bg-slate-950 px-3 py-2 gap-2 border-b border-slate-800 overflow-x-auto text-xs">
              {(['NativeHomeScreenNew', 'AddScreen', 'Theme', 'MainActivity'] as const).map((tab) => (
                <button
                  key={tab}
                  onClick={() => setSelectedInspectorFile(tab)}
                  className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                    selectedInspectorFile === tab ? 'bg-[#1E40AF] text-white' : 'bg-slate-800 text-slate-300'
                  }`}
                >
                  {tab}.kt
                </button>
              ))}
            </div>

            <div className="flex-1 p-4 overflow-auto font-mono text-xs bg-slate-950 text-slate-200 select-all leading-relaxed" dir="ltr">
              {selectedInspectorFile === 'NativeHomeScreenNew' && (
                <pre>
{`// app/src/main/java/com/example/NativeHomeScreenNew.kt
// 1. المساحة العلوية: statusBarsPadding() لمنع التداخل مع شريط حالة Android.
// 2. المساحة السفلية: navigationBarsPadding() و contentPadding كافية لمنع اختفاء آخر عنصر.
// 3. الأيقونات العلوية: أيقونة المحل وأيقونة الدراجة النارية بصندوق التوصيل والوضع الليلي (مقاس موحد 34dp ومسافات متساوية 6dp).
// 4. الشريط السفلي (5 عناصر مرتبة): الرئيسية، العروض، إضافة، الليفرورات، حسابي.
// 5. الوضمان المستقلان: الأزرق الداكن الملكي لصاحب المحل (#1E40AF) والبرتقالي لليفرور (#EA580C).`}
                </pre>
              )}
              {selectedInspectorFile === 'AddScreen' && (
                <pre>
{`// app/src/main/java/com/example/AddScreen.kt
// يتكيف تلقائياً مع دور المستخدم (طلب توصيل للمحل vs عرض توصيل لليفرور)
// يحترم المساحة العلوية والسفلية (statusBarsPadding / navigationBarsPadding)`}
                </pre>
              )}
              {selectedInspectorFile === 'Theme' && (
                <pre>
{`// app/src/main/java/com/example/ui/theme/Theme.kt
// نظام ألوان مركزي: Blue الملكي الداكن (ShopBluePrimary #1E40AF) و Orange (DriverOrangePrimary #EA580C)
// خلفية مساحة محتوى مريحة للعين في الوضع الداكن (#0A0F1D) مع تباين واضح للبطاقات (#162032) والنصوص.`}
                </pre>
              )}
              {selectedInspectorFile === 'MainActivity' && (
                <pre>
{`// app/src/main/java/com/example/MainActivity.kt
// استخدام enableEdgeToEdge()
// حفظ واسترجاع اختيار الدور في SharedPreferences`}
                </pre>
              )}
            </div>

            <div className="p-3 bg-slate-900 border-t border-slate-800 text-xs text-slate-400 flex items-center justify-between">
              <span>جميع التعديلات مطبقة في مسارات Android الأصلية وجاهزة للبناء</span>
              <button onClick={() => setShowCodeInspector(false)} className="px-3 py-1 bg-[#1E40AF] hover:bg-blue-900 text-white rounded-lg font-bold">
                إغلاق
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
