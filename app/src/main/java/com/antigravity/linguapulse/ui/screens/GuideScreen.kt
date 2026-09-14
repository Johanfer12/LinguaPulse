package com.antigravity.linguapulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.linguapulse.ui.theme.TagB2B
import com.antigravity.linguapulse.ui.theme.TagDaily

// Constante de nivel superior: antes se creaba una lista nueva en cada recomposicion.
private val TAB_TITLES = listOf("Tiempos & Modales", "Reglas & Trucos", "Ventas B2B & Tips")

@Composable
fun GuideScreen(
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📖 Guía de Conversación y Fórmulas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Reglas gramaticales esenciales, tablas de modales y tips de conversación",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    TAB_TITLES.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> TensesAndModalsTab(onSpeak = onSpeak)
                1 -> GrammarRulesAndTricksTab(onSpeak = onSpeak)
                2 -> B2bSalesAndTipsTab(onSpeak = onSpeak)
            }
        }
    }
}

// =========================================================================
// TAB 1: TIEMPOS VERBALES Y MODALES
// =========================================================================
@Composable
private fun TensesAndModalsTab(onSpeak: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GuideExpandableCard(
                title = "Tiempos Verbales en Inglés (Fórmulas)",
                subtitle = "Presente, Pasado y Futuro con sus 4 estructuras",
                icon = Icons.Default.Timeline,
                accentColor = TagDaily,
                defaultExpanded = true
            ) {
                Text(
                    text = "1. Presente (P)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                FormulaRow("PS (Present Simple)", "S + do/does (?-) + V (base)", "I work every day.")
                FormulaRow("PC (Present Continuous)", "S + am/is/are + V-ing", "She is working now.")
                FormulaRow("PP (Present Perfect)", "S + have/has + V (participio)", "They have worked here.")
                FormulaRow("PPC (Present Perfect Cont.)", "S + have/has been + V-ing", "I have been working.")

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "2. Pasado (P)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                FormulaRow("PS (Past Simple)", "S + did (-?) → V (pasado)", "We deployed the code.")
                FormulaRow("PC (Past Continuous)", "S + was/were + V-ing", "He was analyzing data.")
                FormulaRow("PP (Past Perfect)", "S + had + V (participio)", "The server had crashed.")
                FormulaRow("PPC (Past Perfect Cont.)", "S + had been + V-ing", "They had been testing.")

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "3. Futuro (F)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                FormulaRow("FS (Future Simple)", "S + will (+?) / won't (-) + V", "We will scale up.")
                FormulaRow("FC (Future Continuous)", "S + will + be + V-ing", "I will be presenting.")
                FormulaRow("FP (Future Perfect)", "S + will have + V (participio)", "We will have finished.")
                FormulaRow("FPC (Future Perfect Cont.)", "S + will have been + V-ing", "She will have been leading.")
            }
        }

        item {
            GuideExpandableCard(
                title = "Tabla de Traducción / Sentido Común",
                subtitle = "¿Cómo se traduce mentalmente cada tiempo?",
                icon = Icons.Default.Translate,
                accentColor = MaterialTheme.colorScheme.secondary
            ) {
                TenseTranslationTable()
            }
        }

        item {
            GuideExpandableCard(
                title = "Verbos Modales y Ejemplos",
                subtitle = "Significado y uso de Can, Could, May, Must, etc.",
                icon = Icons.Default.Extension,
                accentColor = TagB2B
            ) {
                ModalRow("Can", "Habilidad / Permiso informal", "She can swim.", "¿Ella puede nadar?", onSpeak)
                ModalRow("Could", "Habilidad en el pasado / Posibilidad", "He could run fast.", "Él podía correr rápido.", onSpeak)
                ModalRow("May", "Permiso formal / Posibilidad", "May I come in?", "¿Puedo entrar?", onSpeak)
                ModalRow("Might", "Posibilidad (menos probable)", "It might rain.", "Podría llover.", onSpeak)
                ModalRow("Must", "Obligación / Deducción lógica", "You must study.", "Debes estudiar. / Seguro estudias.", onSpeak)
                ModalRow("Shall", "Sugerencia / Ofrecimiento formal (UK)", "Shall we begin?", "¿Empezamos?", onSpeak)
                ModalRow("Should", "Consejo / Recomendación", "You should sleep early.", "Deberías dormir temprano.", onSpeak)
                ModalRow("Will", "Futuro / Promesa", "I will call you.", "Te llamaré.", onSpeak)
                ModalRow("Would", "Condicional / Cortesía", "I would like some tea.", "Me gustaría un poco de té.", onSpeak)
                ModalRow("Ought to", "Consejo / Recomendación moral", "You ought to help them.", "Deberías ayudarlos.", onSpeak)
                ModalRow("Need to", "Necesidad", "You need to see this.", "Necesitas ver esto.", onSpeak)
                ModalRow("Have to", "Obligación externa", "I have to go to work.", "Tengo que ir al trabajo.", onSpeak)
                ModalRow("Used to", "Hábito en el pasado", "I used to play piano.", "Solía tocar el piano.", onSpeak)
                ModalRow("Be able to", "Capacidad (en varios tiempos)", "I was able to finish it.", "Fui capaz de terminarlo.", onSpeak)
            }
        }

        item {
            GuideExpandableCard(
                title = "Cómo NO Mezclar Modales",
                subtitle = "Regla: Solo 1 modal a la vez + alternativas correctas",
                icon = Icons.Default.Warning,
                accentColor = Color(0xFFEF4444)
            ) {
                Text(
                    text = "⚠️ Reglas de Oro de los Modales:\n• Solo se puede usar 1 modal a la vez.\n• Se puede usar un modal seguido de un auxiliar (en ese orden).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                NoMixRow("❌ will can", "✅ will be able to")
                NoMixRow("❌ may can", "✅ may be able to")
                NoMixRow("❌ could can", "✅ could be able to")
                NoMixRow("❌ could may", "✅ was/were allowed to")
                NoMixRow("❌ might could", "✅ might have been able to")
                NoMixRow("❌ must should", "✅ have to / need to / ought to")
                NoMixRow("❌ should must", "✅ really should / ought to")
            }
        }

        item {
            GuideExpandableCard(
                title = "Auxiliares (Be, Have, Do)",
                subtitle = "Uso principal y el 'Do' enfático",
                icon = Icons.Default.FlashOn,
                accentColor = MaterialTheme.colorScheme.primary
            ) {
                AuxRow("Be", "Formar tiempos continuos y voz pasiva", "She is dancing. / It was done.", onSpeak)
                AuxRow("Have", "Formar tiempos perfectos", "They have arrived.", onSpeak)
                AuxRow("Do", "Formar preguntas, negaciones y ÉNFASIS", "Do you like it? / I don't know. / I DO like it (¡Sí me gusta!)", onSpeak)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 Tip del 'Do' Enfático: En ventas o conversaciones cuando alguien duda de ti, di 'I DO believe this is the best solution' (¡De verdad creo que es la mejor solución!).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// =========================================================================
// TAB 2: REGLAS PRÁCTICAS Y TRUCOS
// =========================================================================
@Composable
private fun GrammarRulesAndTricksTab(onSpeak: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GuideExpandableCard(
                title = "Regla de Oro: \"To\" + Verbo vs Verbo + \"-ing\"",
                subtitle = "¿Cuándo usar infinitivo y cuándo usar gerundio?",
                icon = Icons.Default.CheckCircle,
                accentColor = Color(0xFF10B981),
                defaultExpanded = true
            ) {
                Text(
                    text = "🎯 REGLA RÁPIDA:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Si el verbo anterior expresa una DECISIÓN, DESEO o PROMESA ➔ usa 'to' + verbo base.\n• Si el verbo anterior expresa una EMOCIÓN, PREFERENCIA o ACTIVIDAD ➔ usa verbo + '-ing'.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "📋 FÓRMULA RESUMIDA:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                FormulaSummaryRow("Un deseo, decisión o promesa", "to + verbo", "I want to go. / I decide to stay.")
                FormulaSummaryRow("Una emoción o preferencia", "verb + ing", "I enjoy reading. / I hate waiting.")
                FormulaSummaryRow("Una PREPOSICIÓN antes del verbo", "verb + ing", "I'm good at drawing. / Before leaving.")

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "🧠 TRUCO DE MEMORIA:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "• To = Objetivos o intenciones a futuro (Quiero hacer algo) ➔ I hope to win.\n• -ing = Actividades o acciones en sí mismas (Disfrutar la acción) ➔ I enjoy winning.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        item {
            GuideExpandableCard(
                title = "Preposiciones de Tiempo y Lugar (IN - ON - AT)",
                subtitle = "De lo general a lo hiper-específico",
                icon = Icons.Default.LocationOn,
                accentColor = TagDaily,
                defaultExpanded = true
            ) {
                Text(
                    text = "Pirámide IN ➔ ON ➔ AT (General a Específico):",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                PrepBox("IN (Muy General / Espacios Grandes)", "• Meses: in July, in December\n• Años: in 2026, in the 90s\n• Partes del día: in the morning, in the evening (excepción: at night)\n• Países y ciudades: in Colombia, in New York")
                Spacer(modifier = Modifier.height(6.dp))
                PrepBox("ON (Intermedio / Días y Superficies)", "• Días de la semana: on Monday, on Friday\n• Fechas específicas: on April 10th\n• Superficies: on the table, on the floor\n• Medios de transporte públicos: on the bus, on the train, on the plane (excepción: in the car)")
                Spacer(modifier = Modifier.height(6.dp))
                PrepBox("AT (Hiper Específico / Horas y Puntos)", "• Horas exactas: at 7:00 AM, at noon\n• Lugares específicos: at the door, at the entrance\n• Eventos: at the party, at the conference\n• Excepción común: at night, at the weekend (UK)")
            }
        }

        item {
            GuideExpandableCard(
                title = "Much vs. Many",
                subtitle = "¿Cuándo usar incontables y cuándo contables?",
                icon = Icons.Default.Calculate,
                accentColor = TagB2B
            ) {
                Text(
                    text = "• MUCH ➔ Mucho / ¿Cuánto? (Para sustantivos INCONTABLES que no se pueden pluralizar):\n  Ejemplos: How much money?, much water, much time, much data, much progress.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• MANY ➔ Muchos / ¿Cuántos? (Para sustantivos CONTABLES que terminan en -s o son plurales):\n  Ejemplos: How many dollars?, many books, many hours, many servers, many clients.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 Truco salvador: Si dudas si algo es contable o no, usa 'A lot of' (sirve para ambos: a lot of time / a lot of books).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// =========================================================================
// TAB 3: VENTAS B2B & TIPS PRO
// =========================================================================
@Composable
private fun B2bSalesAndTipsTab(onSpeak: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GuideExpandableCard(
                title = "Estructura TED para Preguntas en Discovery (B2B)",
                subtitle = "Cómo hacer preguntas abiertas que descubran presupuestos y dolores",
                icon = Icons.Default.QuestionAnswer,
                accentColor = TagB2B,
                defaultExpanded = true
            ) {
                Text(
                    text = "En ventas consultivas de Cloud y AI, evita preguntas cerradas de Sí/No. Usa el marco TED:",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(10.dp))

                TedItem("T - Tell me...", "Tell me about how your engineering team is handling cluster provisioning right now.", onSpeak)
                TedItem("E - Explain to me...", "Explain to me the impact on query latency when traffic spikes during month-end.", onSpeak)
                TedItem("D - Describe...", "Describe what ideal success looks like for this generative AI rollout in Q4.", onSpeak)
            }
        }

        item {
            GuideExpandableCard(
                title = "Suavizadores Diplomáticos (Corporate Softeners)",
                subtitle = "Cómo sonar profesional, persuasivo y nada agresivo",
                icon = Icons.Default.Handshake,
                accentColor = MaterialTheme.colorScheme.secondary
            ) {
                SoftenerRow("❌ Directo: You must change your pipeline.", "✅ Diplomático: Would it make sense to explore optimizing the pipeline?", onSpeak)
                SoftenerRow("❌ Directo: That's wrong.", "✅ Diplomático: From our experience with similar enterprise setups...", onSpeak)
                SoftenerRow("❌ Directo: Give me your budget.", "✅ Diplomático: What ballpark range are you considering for this project?", onSpeak)
                SoftenerRow("❌ Directo: Do you agree?", "✅ Diplomático: How does that resonate with your technical roadmap?", onSpeak)
            }
        }

        item {
            GuideExpandableCard(
                title = "Falsos Amigos Frecuentes en Negocios (False Friends)",
                subtitle = "Palabras que parecen español pero significan otra cosa",
                icon = Icons.Default.Dangerous,
                accentColor = Color(0xFFF59E0B)
            ) {
                FalseFriendRow("Actually", "Significa 'En realidad / De hecho'", "NO 'Actualmente' (usa 'Currently')")
                FalseFriendRow("Compromise", "Significa 'Llegar a un acuerdo / Ceder'", "NO solo 'Compromiso' (usa 'Commitment')")
                FalseFriendRow("Realize", "Significa 'Darse cuenta'", "NO 'Realizar un evento' (usa 'Carry out / Perform')")
                FalseFriendRow("Eventually", "Significa 'Al final / Tarde o temprano'", "NO 'Eventualmente / Ocasionalmente' (usa 'Occasionally')")
                FalseFriendRow("Resume", "Significa 'Currículum / Reanudar'", "NO 'Resumen' (usa 'Summary / Brief')")
            }
        }

        item {
            GuideExpandableCard(
                title = "Manejo de Objeciones: Método Feel - Felt - Found",
                subtitle = "Fórmula psicológica para desarmar objeciones de seguridad o precio",
                icon = Icons.Default.Shield,
                accentColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "Cuando el cliente dice 'Nuestra política interna de datos no permite soluciones SaaS externas':",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. FEEL (Empatía): 'I completely understand how you feel regarding strict data sovereignty.'\n\n2. FELT (Validación): 'Several CISOs at tier-1 financial institutions felt the exact same hesitation initially.'\n\n3. FOUND (Solución probada): 'What they found was that with our Customer-Managed Encryption Keys (CMEK) and VPC peering, data never leaves their secure perimeter.'",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// =========================================================================
// HELPER SUB-COMPONENTS
// =========================================================================

@Composable
private fun GuideExpandableCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    defaultExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Colapsar" else "Expandir"
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun FormulaRow(name: String, formula: String, example: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(
            text = formula,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Ej: $example",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun TenseTranslationTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Estructura", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), fontSize = 12.sp)
            Text("Presente", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text("Pasado", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text("Futuro", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 12.sp)
        }
        Divider(modifier = Modifier.padding(vertical = 6.dp))
        TableRowContent("Simple", "Que pasan", "Que pasaron", "Que pasarán")
        TableRowContent("Continuo (Be)", "Que están pasando", "Que estuvieron pasando", "Que estarán pasando")
        TableRowContent("Perfecto (Have)", "Que han pasado", "Que habían pasado", "Que habrán pasado")
        TableRowContent("Perf. Continuo", "Que han estado pasando", "Que habían estado pasando", "Que habrán estado pasando")
    }
}

@Composable
private fun TableRowContent(col1: String, col2: String, col3: String, col4: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(col1, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
        Text(col2, modifier = Modifier.weight(1f), fontSize = 11.sp)
        Text(col3, modifier = Modifier.weight(1f), fontSize = 11.sp)
        Text(col4, modifier = Modifier.weight(1f), fontSize = 11.sp)
    }
}

@Composable
private fun ModalRow(
    modal: String,
    use: String,
    exampleEn: String,
    meaningEs: String,
    onSpeak: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = modal,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = use,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "“$exampleEn” ➔ $meaningEs",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
        IconButton(onClick = { onSpeak(exampleEn) }, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun NoMixRow(wrong: String, correct: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(wrong, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Text(correct, color = Color(0xFF10B981), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AuxRow(name: String, use: String, example: String, onSpeak: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "($use)", style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Ej: $example", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            IconButton(onClick = { onSpeak(example) }, modifier = Modifier.size(28.dp)) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun FormulaSummaryRow(trigger: String, use: String, example: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(trigger, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
            Text(use, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        }
        Text("➔ $example", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun PrepBox(title: String, body: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = body, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun TedItem(ted: String, sentence: String, onSpeak: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(ted, fontWeight = FontWeight.Bold, color = TagB2B, style = MaterialTheme.typography.labelLarge)
            Text("“$sentence”", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
        IconButton(onClick = { onSpeak(sentence) }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SoftenerRow(direct: String, soft: String, onSpeak: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(direct, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(soft, style = MaterialTheme.typography.bodySmall, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            IconButton(onClick = { onSpeak(soft.replace("✅ Diplomático: ", "")) }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun FalseFriendRow(word: String, meaning: String, warning: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(word, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
        Text(meaning, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        Text(warning, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    }
}
