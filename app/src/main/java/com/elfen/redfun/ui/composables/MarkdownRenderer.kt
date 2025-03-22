package com.elfen.redfun.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.intellij.markdown.MarkdownElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

@Composable
fun MarkdownRenderer(modifier: Modifier = Modifier, content: String) {
    val elements = remember {
        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(content.trimMargin())

        var elements = parseElement(content.trimMargin(), parsedTree)

        elements
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        elements.forEach { (element, type) ->
            if (type === "text" && element.isNotEmpty()) Text(
                element, modifier = Modifier.fillMaxWidth(), inlineContent = mapOf(
                    Pair(
                        "https://octodex.github.com/images/minion.png",
                        InlineTextContent(
                            Placeholder(
                                120.sp, 120.sp,
                                PlaceholderVerticalAlign.TextCenter
                            )
                        ) {
                            AsyncImage(
                                model = "https://octodex.github.com/images/minion.png",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    ),
                    Pair(
                        "https://octodex.github.com/images/stormtroopocat.jpg",
                        InlineTextContent(
                            Placeholder(
                                120.sp, 120.sp,
                                PlaceholderVerticalAlign.TextCenter
                            )
                        ) {
                            AsyncImage(
                                model = "https://octodex.github.com/images/stormtroopocat.jpg",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    )
                )
            )
            else if (type === "line") {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 2.dp
                )
            } else if (type === "blockquote" && element.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer
                        ),
                ) {
                    VerticalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        element,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }
            } else if (type === "code_block") {
                Box(
                    modifier = Modifier
                        .background(
                            Color.LightGray, RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        element, modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun parseElement(text: String, element: ASTNode): List<Pair<AnnotatedString, String>> {
    val elements = emptyList<Pair<AnnotatedString, String>>().toMutableList()

    if (element.type === MarkdownElementTypes.MARKDOWN_FILE) {
        element.children.forEach { element ->
            val type = when (element.type) {
                MarkdownElementTypes.BLOCK_QUOTE -> "blockquote"
                MarkdownTokenTypes.HORIZONTAL_RULE -> "line"
                MarkdownElementTypes.CODE_FENCE -> "code_block"
                MarkdownElementTypes.CODE_BLOCK -> "code_block"
                else -> "text"
            }

            if (element.type === MarkdownElementTypes.CODE_BLOCK || element.type === MarkdownElementTypes.CODE_FENCE)
                elements += Pair(parseBlock(text, listOf(element)), type)
            else if (element.type === GFMElementTypes.TABLE)
                elements += Pair(buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.Red)) {
                        append(
                            "TABLES: TODO"
                        )
                    }
                }, "text")
            else if (
                element.children.isNotEmpty() &&
                element.children.all { it.type == MarkdownTokenTypes.EOL }.not()
            ) elements += Pair(parseBlock(text, element.children), type)
        }
    }

    return elements
}

private fun parseBlock(text: String, elements: List<ASTNode>, depth: Int = 0): AnnotatedString {
    return buildAnnotatedString {
        elements.forEach {

            if (it.type === MarkdownTokenTypes.TEXT) {
                val content = it.getTextInNode(text).trim()
                append(content)
            } else if (it.type === MarkdownTokenTypes.CODE_FENCE_CONTENT || it.type === MarkdownTokenTypes.CODE_LINE) {
                append(it.getTextInNode(text))
            } else if (it.type === MarkdownTokenTypes.EOL && (it.parent?.type === MarkdownElementTypes.CODE_BLOCK || it.parent?.type === MarkdownElementTypes.CODE_FENCE)) {
                append("\n")
            } else if (it.type === MarkdownTokenTypes.WHITE_SPACE) {
                append(" ")
            } else if (it.type === MarkdownTokenTypes.EMPH && (it.type as MarkdownElementType).isToken && it.parent?.type !== MarkdownElementTypes.EMPH && it.parent?.type !== MarkdownElementTypes.STRONG) {
                append("*")
            } else if (it.type === MarkdownElementTypes.EMPH || it.type === MarkdownElementTypes.STRONG) {
                if (!(it.type as MarkdownElementType).isToken) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(parseBlock(text, it.children))
                    }
                }
            } else if (it.type === MarkdownElementTypes.BLOCK_QUOTE && !(it.type as MarkdownElementType).isToken) {
                append(parseBlock(text, it.children));
            } else if ((it.parent?.type === MarkdownElementTypes.BLOCK_QUOTE) && it.type === MarkdownElementTypes.PARAGRAPH) {
                append(parseBlock(text, it.children))
            } else if (it.type === MarkdownElementTypes.LIST_ITEM) {
                withStyle(
                    ParagraphStyle(
                        textIndent = TextIndent(
                            ((depth + 1) * 12).sp, ((depth + 1) * 12).sp
                        )
                    )
                ) {
                    append(parseBlock(text, it.children))
                }
            } else if (it.type === MarkdownTokenTypes.LIST_BULLET) {
                append("• ")
            } else if (it.type === MarkdownTokenTypes.LIST_NUMBER) {
                append("${it.getTextInNode(text)}")
            } else if (it.type === MarkdownElementTypes.UNORDERED_LIST) {
                append(parseBlock(text, it.children, depth + 1))
            } else if (it.type === MarkdownElementTypes.PARAGRAPH) {
                append(parseBlock(text, it.children))
            } else if (it.type === MarkdownElementTypes.INLINE_LINK) {
                val destination =
                    it.children.find { it.type === MarkdownElementTypes.LINK_DESTINATION }
                        ?.getTextInNode(text)
                val linkText = it.children.find { it.type === MarkdownElementTypes.LINK_TEXT }

                withStyle(SpanStyle(color = Color.Blue)) {
                    withLink(LinkAnnotation.Url(destination.toString())) {
                        append(parseBlock(text, linkText!!.children))
                    }
                }
            } else if (it.type === MarkdownTokenTypes.LPAREN) {
                append("(")
            } else if (it.type === MarkdownTokenTypes.RPAREN) {
                append(")")
            } else if (it.type === MarkdownTokenTypes.COLON) {
                append(":")
            } else if (it.type === MarkdownTokenTypes.DOUBLE_QUOTE) {
                append("\"")
            } else if (it.type === MarkdownTokenTypes.SINGLE_QUOTE) {
                append("'")
            } else if (it.type === MarkdownTokenTypes.ATX_CONTENT) {
                val fontSize = when (it.parent!!.type) {
                    MarkdownElementTypes.ATX_1 -> 32.sp
                    MarkdownElementTypes.ATX_2 -> 24.sp
                    MarkdownElementTypes.ATX_3 -> 18.sp
                    MarkdownElementTypes.ATX_4 -> 14.sp
                    MarkdownElementTypes.ATX_5 -> 12.sp
                    MarkdownElementTypes.ATX_6 -> 10.sp
                    else -> 14.sp
                }

                val lineHeight = when (it.parent!!.type) {
                    MarkdownElementTypes.ATX_1 -> 36.sp
                    MarkdownElementTypes.ATX_2 -> 28.sp
                    MarkdownElementTypes.ATX_3 -> 22.sp
                    MarkdownElementTypes.ATX_4 -> 18.sp
                    MarkdownElementTypes.ATX_5 -> 16.sp
                    MarkdownElementTypes.ATX_6 -> 14.sp
                    else -> 16.sp
                }

                withStyle(ParagraphStyle(lineHeight = lineHeight)) {
                    withStyle(SpanStyle(fontSize = fontSize, fontWeight = FontWeight.Bold)) {
                        append(
                            parseBlock(
                                text, it.children.drop(1)
                            )
                        )
                    }
                }
            } else if (it.type === MarkdownElementTypes.CODE_SPAN) {
                withStyle(
                    SpanStyle(
                        background = Color.LightGray,
                        fontFamily = FontFamily.Monospace,
                        color = Color.DarkGray
                    )
                ) {
                    append(" ")
                    append(parseBlock(text, it.children))
                    append(" ")
                }
            } else if (it.type === MarkdownElementTypes.CODE_FENCE) {
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                    append(
                        parseBlock(
                            text,
                            it.children
                                .filter { it.type !== MarkdownTokenTypes.CODE_FENCE_START && it.type !== MarkdownTokenTypes.CODE_FENCE_END && it.type !== MarkdownTokenTypes.FENCE_LANG }
                                .drop(1).dropLast(1)
                        )
                    )
                }
            } else if (it.type === MarkdownElementTypes.CODE_BLOCK) {
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                    append(
                        parseBlock(text, it.children)
                    )
                }
            } else if (it.type === MarkdownElementTypes.IMAGE) {
                val inlineLink =
                    it.children.find { it.type === MarkdownElementTypes.INLINE_LINK }
                val fullReferenceLink =
                    it.children.find { it.type === MarkdownElementTypes.FULL_REFERENCE_LINK }

                if (fullReferenceLink != null) {
                    val linkLabel =
                        fullReferenceLink.children.find { it.type === MarkdownElementTypes.LINK_LABEL }!!
                            .getTextInNode(text)
                    val linkText =
                        fullReferenceLink.children.find { it.type === MarkdownElementTypes.LINK_TEXT }!!
                            .getTextInNode(text)

                    append("\n")
                    append("IMAGE \"$linkLabel\": $linkText")
                    appendInlineContent(linkText.toString(), linkLabel.toString())
                    append("\n")
                } else if (inlineLink != null) {
                    val destination =
                        inlineLink.children.find { it.type === MarkdownElementTypes.LINK_DESTINATION }!!
                            .getTextInNode(text)
                    val linkText =
                        inlineLink.children.find { it.type === MarkdownElementTypes.LINK_TEXT }!!
                            .getTextInNode(text)

                    append("\n")
                    withStyle(ParagraphStyle()) {
                        appendInlineContent(
                            destination.toString(),
                            linkText.toString()
                        )
                    }
                    append("\n")
                }
            }
        }
    }
}
