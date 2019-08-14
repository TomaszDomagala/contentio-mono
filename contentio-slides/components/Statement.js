import React, { Fragment } from "react";
import { markdownToHtml } from "../utils/markdownUtils";
import { Box, Text } from "rebass";

String.prototype.replaceAll = function(search, replacement) {
	var target = this;
	return target.replace(new RegExp(search, "g"), replacement);
};

const Statement = props => {
	const { sentences, slide } = props;

	// https://stackoverflow.com/a/34890276
	const groupBy = function(xs, key) {
		return xs.reduce(function(rv, x) {
			(rv[x[key]] = rv[x[key]] || []).push(x);
			return rv;
		}, {});
	};

	const sentencesToParagraphs = sentences => {
		const paragraphs = groupBy(sentences, "paragraph");
		return Object.values(paragraphs);
	};

	const mapSentencesToText = (sentences, slide, props) => {
		return sentences.map(({ text, index }) => {
			const visible = index <= slide;
			const htmlText = markdownToHtml(text + " ")
				.replaceAll("<p>", "")
				.replaceAll("</p>", "");
			return (
				<Text
					color={visible ? "white" : "black"}
					style={{ display: "inline" }}
					dangerouslySetInnerHTML={{ __html: htmlText }}
					key={index}
					{...props}
				/>
			);
		});
	};

	return (
		<Box bg="black" style={{ float: "left" }}>
			{sentencesToParagraphs(sentences).map((sent,i) => (
				<Box key={i} pb={2}>{mapSentencesToText(sent, slide, props)}</Box>
			))}
		</Box>
	);
};

export default Statement;
