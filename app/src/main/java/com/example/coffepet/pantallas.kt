package com.example.coffepet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.ArrowBack
import androidx.core.content.ContextCompat
import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


data class TiendaItem(
    val nombre: String,
    val descripcion: String,
    val costo: Int,
    val icon: Int
)

// 🟤 LOGIN SCREEN
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var users by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(Unit) {
        userPrefs.getUsers().collect { saved -> users = saved }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Filtro oscuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA3E2723))
        )

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Bienvenido a CoffeePet ☕",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = Color.White) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33FFFFFF), RoundedCornerShape(8.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.White) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33FFFFFF), shape = RoundedCornerShape(8.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        val userPassword = users[email]
                        if (userPassword != null && userPassword == password) {
                            navController.navigate("home/$email")
                        } else showError = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
                ) { Text("Sign in", color = Color.White) }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { navController.navigate("home/$email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437))
                ) { Text("Iniciar sesión con Google", color = Color.White) }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { navController.navigate("home/$email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                ) { Text("Iniciar sesión con Facebook", color = Color.White) }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        scope.launch {
                            when {
                                email.isBlank() && password.isBlank() ->
                                    snackbarHostState.showSnackbar("Ingresa un correo y una contraseña ☕")
                                email.isBlank() ->
                                    snackbarHostState.showSnackbar("El campo de correo está vacío 💬")
                                password.isBlank() ->
                                    snackbarHostState.showSnackbar("El campo de contraseña está vacío 🔒")
                                users.containsKey(email) ->
                                    snackbarHostState.showSnackbar("El correo ya está registrado ☕")
                                else -> {
                                    userPrefs.saveUser(email, password)
                                    snackbarHostState.showSnackbar("Usuario registrado correctamente 💜")
                                    email = ""; password = ""
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037))
                ) { Text("Registrar nuevo usuario", color = Color.White) }

                if (showError) {
                    LaunchedEffect(Unit) {
                        snackbarHostState.showSnackbar("Correo o contraseña incorrectos")
                        showError = false
                    }
                }
            }
        }
    }
}


// 🟤 HOME SCREEN
@Composable
fun HomeScreen(navController: NavController, email: String) {
    val homeNavController = rememberNavController()
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val userCoinsFlow = userPrefs.getCoinsForUser(email)
    val userCoins: Int by userCoinsFlow.collectAsState(initial = 0)
    val onCoinChange: (Int) -> Unit = { newCoins ->
        scope.launch {
            userPrefs.saveCoinsForUser(email, newCoins)
        }
    }

    var userLevel by remember { mutableStateOf(1) }
    var currentExp by remember { mutableStateOf(40f) }
    var maxExp by remember { mutableStateOf(100f) }

    // 🎩 Estado global del gorro
    var selectedHat by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color(0x993E2723)))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            MascotaIdle(
                modifier = Modifier.offset(y = (-100).dp),
                selectedHat = selectedHat
            )
        }

        Scaffold(
            topBar = {
                TopBar(
                    userLevel = userLevel,
                    currentExp = currentExp,
                    maxExp = maxExp,
                    userCoins = userCoins,
                    onSettingsClick = { homeNavController.navigate("configuracion") }
                )
            },
            bottomBar = { BottomNavigationBar(homeNavController) },
            containerColor = Color.Transparent
        ) { innerPadding ->
            NavHost(
                navController = homeNavController,
                startDestination = "mascota",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("historial") { HistorialScreen(homeNavController) }
                composable("tienda") {
                    TiendaScreen(
                        userCoins = userCoins,
                        onCoinChange = onCoinChange,
                        selectedHat = selectedHat,
                        onHatSelected = { selectedHat = it },
                        homeNavController = homeNavController
                    )
                }

                composable("mascota") { MascotaScreen() }
                composable("mapa") { MapaScreen(navController = navController) }
                composable("perfil") { PerfilScreen() }
                composable("configuracion") { ConfiguracionScreen(navController) }
                composable("escanear") { EscanearScreen(homeNavController) }
            }
        }
    }
}


// 🟤 NAVBAR
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Historial", "historial"),
        BottomNavItem("Tienda", "tienda"),
        BottomNavItem("Escanear", "escanear"),
        BottomNavItem("Mapa", "mapa"),
        BottomNavItem("Perfil", "perfil")
    )

    NavigationBar(containerColor = Color(0xFF3E2723), contentColor = Color.White) {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.route == item.route,
                onClick = { navController.navigate(item.route) },
                icon = {},
                label = { Text(item.label, color = Color.White, fontSize = 12.sp) }
            )
        }
    }
}

data class BottomNavItem(val label: String, val route: String)


// 🟤 TIENDA + ROPERO
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TiendaScreen(
    userCoins: Int,
    onCoinChange: (Int) -> Unit,
    selectedHat: Int?,
    onHatSelected: (Int?) -> Unit,
    homeNavController: NavController
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val descuentos = remember {
        listOf(
            TiendaItem("Café Gratis", "Un café de cualquier tamaño.", 500, R.drawable.ic_launcher_foreground),
            TiendaItem("50% Descuento", "Mitad de precio en cualquier producto.", 1200, R.drawable.ic_launcher_foreground),
            TiendaItem("Accesorio Único", "Un item especial para tu CoffeePet.", 2000, R.drawable.ic_launcher_foreground)
        )
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tienda CoffeePet", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = {
                        homeNavController.navigate("mascota") {
                            popUpTo("tienda") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver al Home", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3E2723)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4E342E))
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(Modifier.height(8.dp))

            MascotaIdle(
                selectedHat = selectedHat,
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Ropero de gorros 🎩",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val hats = listOf(
                    R.drawable.gorro1,
                    R.drawable.gorro2,
                    R.drawable.gorro3,
                    R.drawable.gorro4,
                    R.drawable.gorro5
                )
                hats.forEach { hatRes ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (hatRes == selectedHat)
                                Color(0xFF8D6E63)
                            else Color(0xFF6D4C41)
                        ),
                        modifier = Modifier
                            .size(90.dp)
                            .clickable { onHatSelected(hatRes) }
                    ) {
                        Image(
                            painter = painterResource(id = hatRes),
                            contentDescription = "Gorro",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onHatSelected(null) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037))
            ) {
                Text("Quitar gorro ❌", color = Color.White)
            }

            //DESCUENTOS

            Text(
                "Descuentos",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(descuentos) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D4C41)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(item.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(item.descripcion, color = Color.LightGray, fontSize = 12.sp)
                            }
                            Spacer(Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    if (userCoins >= item.costo) {
                                        onCoinChange(userCoins - item.costo)
                                        scope.launch { snackbarHostState.showSnackbar("¡${item.nombre} comprado! Revisa tu perfil.") }
                                    } else {
                                        scope.launch { snackbarHostState.showSnackbar("Monedas insuficientes. Costo: ${item.costo} ") }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
                                enabled = userCoins >= item.costo
                            ) {
                                Icon(Icons.Default.Star, contentDescription = "Costo", tint = Color(0xFFFFD600), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(item.costo.toString(), color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
// 🟤 OTRAS PANTALLAS
@Composable fun MascotaScreen() { Box(Modifier.fillMaxSize()) }
//Mapa
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(navController: NavController) {
    // 1. Define las coordenadas de tu cafetería (NUEVAS COORDENADAS)
    val cafeLocation = LatLng(-33.499861507690675, -70.61655029657071)

    // 2. Define el estado de la cámara del mapa
    val cameraPositionState = rememberCameraPositionState {
        // Inicializa la cámara en la nueva ubicación con un zoom de 15f
        position = CameraPosition.fromLatLngZoom(cafeLocation, 15f)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mapa de Cafetería", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3E2723)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = cafeLocation),
                title = "Nuestra Cafetería",
                snippet = "¡Visítanos y cuida a tu mascota!"
            )
        }
    }
}
@Composable fun PerfilScreen() { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Pantalla de Perfil", color = Color.White) } }

// 🟤 TOPBAR
@Composable
fun TopBar(userLevel: Int, currentExp: Float, maxExp: Float, userCoins: Int, onSettingsClick: () -> Unit) {
    Surface(
        color = Color(0xFF3E2723),
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth().height(90.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, "Configuraciones", tint = Color.White)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = userCoins.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Default.Star, "Monedas", tint = Color(0xFFFFD600))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Nivel $userLevel", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Box(modifier = Modifier.fillMaxWidth(0.85f).height(14.dp).background(Color(0xFF5D4037), RoundedCornerShape(12.dp))) {
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(currentExp / maxExp).background(Color(0xFFFFD54F), RoundedCornerShape(12.dp)))
                }
            }
        }
    }
}


// 🟤 CONFIGURACION
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(navController: NavController) {
    var notificacionesActivadas by remember { mutableStateOf(true) }
    var volumen by remember { mutableStateOf(5f) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ajustes", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3E2723)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4E342E))
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Recordatorios de visita",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Switch(
                    checked = notificacionesActivadas,
                    onCheckedChange = { notificacionesActivadas = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF795548))
                )
            }
            Divider(color = Color(0xFF6D4C41), thickness = 1.dp)

            Spacer(Modifier.height(24.dp))

            Text(
                "Volumen del Sonido (${volumen.toInt()})",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.VolumeMute,
                    contentDescription = "Volumen bajo",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(8.dp))

                Slider(
                    value = volumen,
                    onValueChange = { volumen = it },
                    // Rango de 0 a 10
                    valueRange = 0f..10f,
                    steps = 9, // 10 valores posibles (0 a 10, 11 pasos)
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF795548),
                        activeTrackColor = Color(0xFF795548),
                        inactiveTrackColor = Color(0xFF6D4C41)
                    )
                )

                Spacer(Modifier.width(8.dp))

                // Icono de volumen (ej: volumen alto)
                Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = "Volumen alto",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Divider(color = Color(0xFF6D4C41), thickness = 1.dp, modifier = Modifier.padding(top = 16.dp))

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("home/{email}") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437))
            ) {
                Text("Cerrar Sesión", color = Color.White)
            }
        }
    }
}


// 🟤 MASCOTA ANIMADA
@Composable
fun MascotaIdle(modifier: Modifier = Modifier, selectedHat: Int? = null) {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetY by infiniteTransition.animateFloat(0f, -8f, infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse))
    val scale by infiniteTransition.animateFloat(1f, 1.05f, infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse))

    Box(
        modifier = modifier
            .size(160.dp)
            .offset(y = offsetY.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.mascota),
            contentDescription = "Mascota CoffeePet",
            modifier = Modifier.fillMaxSize()
        )
        selectedHat?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "Accesorio",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(60.dp)
                    // 🔧 Ajusta estos valores hasta que quede bien
                    .offset(x = 20.dp, y = -30.dp)
            )
        }
    }
}


// 🟤 HISTORIAL
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(homeNavController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de Cafeterías ☕", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = {
                        homeNavController.navigate("mascota") {
                            popUpTo("historial") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, "Volver a Mascota", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF3E2723))
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF4E342E)).padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val historial = listOf(
                    "☕ Café Haru - 20/10/2025",
                    "☕ Sweet Beans - 18/10/2025",
                    "☕ Aroma Urbano - 14/10/2025",
                    "☕ Perfulandia Café - 10/10/2025"
                )
                historial.forEach { lugar ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D4C41)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(lugar, color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscanearScreen(homeNavController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 🔹 Estado para mostrar el QR detectado
    var qrDetectado by remember { mutableStateOf("") }

    // 🔹 Verificar permisos
    var tienePermisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 🔹 Launcher para solicitar permiso
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermisoCamara = concedido
        if (!concedido) {
            Toast.makeText(context, "Permiso de cámara denegado ☕", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔹 Executor de cámara
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Escanear código QR 📷",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        homeNavController.navigate("mascota") {
                            popUpTo("escanear") { inclusive = true }
                        }
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver al Home",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3E2723)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4E342E))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (tienePermisoCamara) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // ✅ Vista previa de la cámara
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }

                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                // 🔹 Analizador QR
                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also {
                                        it.setAnalyzer(cameraExecutor, LectorQr { valorQr ->
                                            qrDetectado = valorQr
                                            Log.d("CoffeePetQR", "Detectado: $valorQr")
                                        })
                                    }

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner, cameraSelector, preview, imageAnalysis
                                    )
                                } catch (e: Exception) {
                                    Log.e("CameraX", "Error al iniciar cámara", e)
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.55f)
                            .clip(MaterialTheme.shapes.medium)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🔹 Mostrar texto del QR detectado
                    Text(
                        text = if (qrDetectado.isNotEmpty())
                            "Código detectado: $qrDetectado"
                        else
                            "Apunta la cámara a un código QR ☕",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                // ❌ Si no tiene permiso
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "La cámara necesita permiso para funcionar ☕",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            permisoLauncher.launch(Manifest.permission.CAMERA)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037))
                    ) {
                        Text("Dar permiso 📸", color = Color.White)
                    }
                }
            }
        }
    }
}