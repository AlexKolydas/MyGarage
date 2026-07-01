package com.alexkolydas.mygarage.feature.garage

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.alexkolydas.mygarage.core.ui.components.DiagonalStripe
import com.alexkolydas.mygarage.core.ui.components.GarageInput
import com.alexkolydas.mygarage.core.ui.components.GarageMultilineInput
import com.alexkolydas.mygarage.core.ui.components.SheetLabel
import com.alexkolydas.mygarage.core.ui.components.UrgencyChip
import com.alexkolydas.mygarage.core.ui.components.garageInputColors
import com.alexkolydas.mygarage.core.ui.model.VehicleUi
import com.alexkolydas.mygarage.core.ui.theme.GarageAccent
import com.alexkolydas.mygarage.core.ui.theme.GarageBackground
import com.alexkolydas.mygarage.core.ui.theme.GarageInputBg
import com.alexkolydas.mygarage.core.ui.theme.GarageSurface
import com.alexkolydas.mygarage.core.ui.theme.GarageSurfaceSheet
import com.alexkolydas.mygarage.core.ui.theme.GarageTextMid
import com.alexkolydas.mygarage.core.ui.theme.GarageTextPrimary
import com.alexkolydas.mygarage.core.ui.theme.GarageTextSecond
import com.alexkolydas.mygarage.core.ui.theme.GarageTextTertiary
import com.alexkolydas.mygarage.core.ui.theme.IbmPlexMonoFamily
import com.alexkolydas.mygarage.core.ui.theme.IbmPlexSansFamily
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import org.koin.androidx.compose.koinViewModel
private const val BANNER_AD_UNIT_ID = "ca-app-pub-9472081319223838/6436484206"

@Composable
fun HomeScreen(
    onNavigateToDetail: (Long) -> Unit,
    vm: GarageViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = BANNER_AD_UNIT_ID
            loadAd(AdRequest.Builder().build())
        }
    }

    LaunchedEffect(vm) {
        vm.effects.collect { effect ->
            when (effect) {
                is GarageContract.Effect.NavigateToDetail ->
                    onNavigateToDetail(effect.vehicleId)
            }
        }
    }

    var photoTargetId by remember { mutableStateOf<Long?>(null) }
    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { u ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    u, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            photoTargetId?.let { id ->
                vm.onIntent(GarageContract.Intent.UpdateVehiclePhoto(id, u.toString()))
            }
        }
        photoTargetId = null
    }

    Box(Modifier.fillMaxSize().background(GarageBackground)) {

        Column(Modifier.fillMaxSize()) {
            Column(Modifier.padding(start = 22.dp, end = 22.dp, top = 18.dp)) {
                Text(
                    text = "MY GARAGE",
                    fontFamily = IbmPlexMonoFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 2.2.sp,
                    color = GarageAccent,
                )
                Text(
                    text = "Garage",
                    fontFamily = IbmPlexSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = (-0.3).sp,
                    color = GarageTextPrimary,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = "${state.vehicles.size} vehicles · tap one for its service log",
                    fontFamily = IbmPlexSansFamily,
                    fontSize = 13.5.sp,
                    color = GarageTextSecond,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Spacer(Modifier.height(16.dp))
                DiagonalStripe()
            }

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, top = 18.dp, bottom = 200.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                if (state.isLoading) {
                    item {
                        Box(
                            Modifier.fillParentMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = GarageAccent)
                        }
                    }
                } else if (state.vehicles.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillParentMaxWidth().padding(top = 40.dp),
                        ) {
                            Text("No vehicles yet", color = GarageTextSecond,
                                fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(
                                "Tap the + button to add your first motorbike.",
                                color = GarageTextTertiary, fontSize = 13.5.sp,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                } else {
                    items(state.vehicles, key = { it.id }) { vui ->
                        VehicleCard(
                            vui = vui,
                            onClick = {
                                vm.onIntent(GarageContract.Intent.VehicleClicked(vui.id))
                            },
                            onLongPress = {
                                vm.onIntent(GarageContract.Intent.LongPressVehicle(vui))
                            },
                            onPhotoTap = {
                                photoTargetId = vui.id
                                photoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        )
                    }
                }
            }
        }

        // Banner ad — sits above the FAB, covered by the backdrop when speed dial opens
        AndroidView(
            factory = { adView },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 100.dp),
        )

        // Backdrop
        AnimatedVisibility(
            visible = state.fabExpanded,
            enter   = fadeIn(),
            exit    = fadeOut(),
            modifier = Modifier.matchParentSize(),
        ) {
            Box(
                Modifier.fillMaxSize()
                    .background(Color(0x9E0A0C0E))
                    .clickable { vm.onIntent(GarageContract.Intent.CloseFab) }
            )
        }

        // Speed-dial items
        AnimatedVisibility(
            visible = state.fabExpanded,
            enter   = fadeIn() + slideInVertically { it / 4 },
            exit    = fadeOut() + slideOutVertically { it / 4 },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 108.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SpeedDialItem(
                    label   = "New vehicle",
                    onClick = { vm.onIntent(GarageContract.Intent.OpenVehicleSheet) },
                ) {
                    Icon(Icons.Default.DirectionsBike, null, tint = GarageTextPrimary,
                        modifier = Modifier.size(22.dp))
                }
                SpeedDialItem(
                    label   = "Add service",
                    onClick = { vm.onIntent(GarageContract.Intent.OpenServiceSheet()) },
                ) {
                    Icon(Icons.Default.Build, null, tint = GarageTextPrimary,
                        modifier = Modifier.size(21.dp))
                }
            }
        }

        // FAB
        val fabRotation by animateFloatAsState(
            targetValue = if (state.fabExpanded) 45f else 0f,
            animationSpec = tween(220, easing = FastOutSlowInEasing),
            label = "fabRotate",
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 28.dp)
                .size(62.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(GarageAccent)
                .clickable { vm.onIntent(GarageContract.Intent.ToggleFab) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Menu",
                tint = GarageBackground,
                modifier = Modifier.size(28.dp).rotate(fabRotation),
            )
        }
    }

    // ── Sheets & dialogs ────────────────────────────────────────────────────────

    when (state.activeSheet) {
        GarageContract.Sheet.NewVehicle ->
            VehicleFormSheet(
                title    = "New vehicle",
                subtitle = "Add a motorbike to your garage.",
                saveText = "Save vehicle",
                state    = state,
                onIntent = vm::onIntent,
            )
        GarageContract.Sheet.EditVehicle ->
            VehicleFormSheet(
                title    = "Edit vehicle",
                subtitle = "Update the details for this bike.",
                saveText = "Save changes",
                state    = state,
                onIntent = vm::onIntent,
            )
        GarageContract.Sheet.AddService ->
            AddServiceSheet(state = state, onIntent = vm::onIntent)
        GarageContract.Sheet.None -> Unit
    }

    // Context menu (long-press)
    state.contextMenuVehicle?.let { vehicle ->
        VehicleContextSheet(
            vehicle  = vehicle,
            onEdit   = { vm.onIntent(GarageContract.Intent.OpenEditVehicleSheet(vehicle)) },
            onDelete = { vm.onIntent(GarageContract.Intent.RequestDeleteVehicle) },
            onDismiss = { vm.onIntent(GarageContract.Intent.DismissContextMenu) },
        )
    }

    // Delete confirmation
    if (state.showDeleteConfirm) {
        val vehicle = state.contextMenuVehicle
        AlertDialog(
            onDismissRequest = { vm.onIntent(GarageContract.Intent.CancelDeleteVehicle) },
            containerColor   = GarageSurfaceSheet,
            title = {
                Text(
                    "Delete ${vehicle?.model ?: "vehicle"}?",
                    fontWeight = FontWeight.Bold,
                    color = GarageTextPrimary,
                )
            },
            text = {
                Text(
                    "This will permanently remove the vehicle and all its service records.",
                    color = GarageTextSecond,
                    fontSize = 14.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = { vm.onIntent(GarageContract.Intent.ConfirmDeleteVehicle) }) {
                    Text("Delete", color = Color(0xFFE05C5C), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.onIntent(GarageContract.Intent.CancelDeleteVehicle) }) {
                    Text("Cancel", color = GarageTextMid)
                }
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vehicle card (tap = navigate, long press = context menu)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VehicleCard(
    vui: VehicleUi,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onPhotoTap: () -> Unit,
) {
    Surface(
        shape    = RoundedCornerShape(18.dp),
        color    = GarageSurface,
        border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongPress),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(GarageInputBg)
                    .clickable(onClick = onPhotoTap),
                contentAlignment = Alignment.Center,
            ) {
                if (vui.photoUri != null) {
                    AsyncImage(
                        model = Uri.parse(vui.photoUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text("Add photo", color = GarageTextTertiary, fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium)
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment    = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = vui.model,
                            fontFamily = IbmPlexSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = GarageTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${vui.name} · ${vui.year}",
                            fontSize = 13.sp,
                            color = GarageTextSecond,
                            modifier = Modifier.padding(top = 1.dp),
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = GarageTextTertiary,
                        modifier = Modifier.size(20.dp).padding(top = 2.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = vui.kmFormatted,
                            fontFamily = IbmPlexMonoFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 23.sp,
                            letterSpacing = (-0.2).sp,
                            color = GarageTextPrimary,
                        )
                        Text(
                            text = "km",
                            fontSize = 12.sp,
                            color = GarageTextSecond,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 2.dp),
                        )
                    }
                    UrgencyChip(vui.urgency)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Speed-dial item
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SpeedDialItem(
    label: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Row(
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Surface(
            shape  = RoundedCornerShape(11.dp),
            color  = GarageSurface,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.09f)),
            shadowElevation = 6.dp,
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                color = GarageTextPrimary,
            )
        }
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF262E35)),
            contentAlignment = Alignment.Center,
        ) { icon() }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Context sheet (shown after long press)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleContextSheet(
    vehicle: VehicleUi,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = GarageSurfaceSheet,
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
    ) {
        Column(Modifier.padding(bottom = 24.dp)) {
            // Vehicle header
            Column(Modifier.padding(horizontal = 22.dp, vertical = 4.dp)) {
                Text(
                    text = vehicle.model,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GarageTextPrimary,
                )
                Text(
                    text = "${vehicle.name} · ${vehicle.year} · ${vehicle.kmFormatted} km",
                    fontSize = 13.sp,
                    color = GarageTextSecond,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
            Spacer(Modifier.height(8.dp))

            ContextMenuItem(
                icon  = Icons.Default.Edit,
                label = "Edit details",
                tint  = GarageTextPrimary,
                onClick = onEdit,
            )
            ContextMenuItem(
                icon  = Icons.Default.Delete,
                label = "Delete vehicle",
                tint  = Color(0xFFE05C5C),
                onClick = onDelete,
            )
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 16.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Text(label, color = tint, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vehicle form sheet (shared by New & Edit)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleFormSheet(
    title: String,
    subtitle: String,
    saveText: String,
    state: GarageContract.State,
    onIntent: (GarageContract.Intent) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onIntent(GarageContract.Intent.CloseSheet) },
        containerColor   = GarageSurfaceSheet,
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
    ) {
        Column(Modifier.padding(horizontal = 22.dp).padding(bottom = 26.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GarageTextPrimary)
            Text(subtitle, color = GarageTextSecond, fontSize = 13.sp,
                modifier = Modifier.padding(top = 3.dp, bottom = 20.dp))

            val form = state.vehicleForm

            SheetLabel("MODEL")
            GarageInput(form.model, { onIntent(GarageContract.Intent.UpdateVehicleModel(it)) }, "e.g. MT-07")
            Spacer(Modifier.height(15.dp))

            SheetLabel("MAKE / NAME")
            GarageInput(form.name, { onIntent(GarageContract.Intent.UpdateVehicleName(it)) }, "e.g. Yamaha")
            Spacer(Modifier.height(15.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                Column(Modifier.weight(1f)) {
                    SheetLabel("YEAR")
                    GarageInput(form.year, { onIntent(GarageContract.Intent.UpdateVehicleYear(it)) }, "2021", mono = true)
                }
                Column(Modifier.weight(1f)) {
                    SheetLabel("KILOMETERS")
                    GarageInput(form.km, { onIntent(GarageContract.Intent.UpdateVehicleKm(it)) }, "18400", mono = true)
                }
            }
            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { onIntent(GarageContract.Intent.CloseSheet) },
                    shape  = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    modifier = Modifier.height(52.dp),
                ) {
                    Text("Cancel", color = GarageTextMid, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick  = { onIntent(GarageContract.Intent.SaveVehicle) },
                    enabled  = form.isValid,
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = GarageAccent, contentColor = GarageBackground,
                        disabledContainerColor = Color(0xFF2A323A),
                        disabledContentColor   = GarageTextTertiary,
                    ),
                    modifier = Modifier.weight(1f).height(52.dp),
                ) {
                    Text(saveText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Add service sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddServiceSheet(
    state: GarageContract.State,
    onIntent: (GarageContract.Intent) -> Unit,
) {
    var vehiclePickerExpanded by remember { mutableStateOf(false) }
    val form = state.serviceForm

    ModalBottomSheet(
        onDismissRequest = { onIntent(GarageContract.Intent.CloseSheet) },
        containerColor   = GarageSurfaceSheet,
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 26.dp),
        ) {
            Text("Add service", fontWeight = FontWeight.Bold, fontSize = 22.sp,
                color = GarageTextPrimary)
            Text("Record maintenance and schedule the next one.",
                color = GarageTextSecond, fontSize = 13.sp,
                modifier = Modifier.padding(top = 3.dp, bottom = 20.dp))

            SheetLabel("VEHICLE")
            val selected = state.vehicles.find { it.id == form.vehicleId }
            ExposedDropdownMenuBox(
                expanded = vehiclePickerExpanded,
                onExpandedChange = { vehiclePickerExpanded = it },
            ) {
                OutlinedTextField(
                    value = selected?.let { "${it.name} ${it.model}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select vehicle", color = GarageTextSecond) },
                    trailingIcon = {
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = GarageTextSecond)
                    },
                    modifier = Modifier.fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape     = RoundedCornerShape(13.dp),
                    colors    = garageInputColors(),
                    textStyle = TextStyle(color = GarageTextPrimary, fontSize = 15.sp),
                )
                ExposedDropdownMenu(
                    expanded = vehiclePickerExpanded,
                    onDismissRequest = { vehiclePickerExpanded = false },
                    containerColor = GarageInputBg,
                ) {
                    state.vehicles.forEach { v ->
                        DropdownMenuItem(
                            text = { Text("${v.name} ${v.model}", color = GarageTextPrimary) },
                            onClick = {
                                onIntent(GarageContract.Intent.SelectServiceVehicle(v.id))
                                vehiclePickerExpanded = false
                            },
                        )
                    }
                }
            }
            Spacer(Modifier.height(15.dp))

            SheetLabel("ODOMETER (KM)")
            GarageInput(form.km, { onIntent(GarageContract.Intent.UpdateServiceKm(it)) },
                "e.g. 18400", mono = true)
            Spacer(Modifier.height(15.dp))

            SheetLabel("WHAT WAS DONE")
            GarageMultilineInput(form.work, { onIntent(GarageContract.Intent.UpdateServiceWork(it)) },
                "e.g. Engine oil & filter change, chain clean and lube.")
            Spacer(Modifier.height(15.dp))

            SheetLabel("NEXT SERVICE AT (KM)")
            GarageInput(form.nextKm, { onIntent(GarageContract.Intent.UpdateServiceNextKm(it)) },
                "e.g. 24000", mono = true)
            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { onIntent(GarageContract.Intent.CloseSheet) },
                    shape  = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    modifier = Modifier.height(52.dp),
                ) {
                    Text("Cancel", color = GarageTextMid, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick  = { onIntent(GarageContract.Intent.SaveService) },
                    enabled  = form.isValid,
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = GarageAccent, contentColor = GarageBackground,
                        disabledContainerColor = Color(0xFF2A323A),
                        disabledContentColor   = GarageTextTertiary,
                    ),
                    modifier = Modifier.weight(1f).height(52.dp),
                ) {
                    Text("Save service", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
