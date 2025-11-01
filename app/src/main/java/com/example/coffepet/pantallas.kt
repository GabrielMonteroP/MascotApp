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

    var userLevel by remember { mutableStateOf(1) }
    var currentExp by remember { mutableStateOf(40f) }
    var maxExp by remember { mutableStateOf(100f) }
    var userCoins by remember { mutableStateOf(9999) }

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
                        selectedHat = selectedHat,
                        onHatSelected = { selectedHat = it }
                    )
                }
                composable("mascota") { MascotaScreen() }
                composable("mapa") { MapaScreen() }
                composable("perfil") { PerfilScreen() }
                composable("configuracion") { ConfiguracionScreen(navController) }
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
        BottomNavItem("Mascota", "mascota"),
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
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TiendaScreen(
    selectedHat: Int?,
    onHatSelected: (Int?) -> Unit
) {
    val hats = listOf(
        R.drawable.gorro1,
        R.drawable.gorro2,
        R.drawable.gorro3,
        R.drawable.gorro4,
        R.drawable.gorro5
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4E342E))
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {



            Text(
                "Tienda CoffeePet ☕",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

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

            // ✅ FlowRow correcto en Compose Foundation
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                hats.forEach { hatRes ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (hatRes == selectedHat) Color(0xFF8D6E63)
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
        }
    }
}


// 🟤 OTRAS PANTALLAS
@Composable fun MascotaScreen() { Box(Modifier.fillMaxSize()) }
@Composable fun MapaScreen() { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Pantalla de Mapa", color = Color.White) } }
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
@Composable
fun ConfiguracionScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF3E2723)),
        contentAlignment = Alignment.Center
    ) {
        Text("Pantalla de Configuración ⚙️", color = Color.White, fontSize = 20.sp)
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
