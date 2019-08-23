import React, { Component, PureComponent } from "react";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";
import ReactResizeDetector from "react-resize-detector";
import { connect } from "react-redux";
import { apiUrl } from "../utils/urls";
import { formatSec } from "../utils/formatting";
import Thumbnail from "../containers/Thumbnail";

class SubmissionView extends PureComponent {
	render() {
		const { details, sentences, currentSentenceIndex } = this.props;

		// console.log(details);
		// console.log(sentences);
		// console.log(currentSentenceIndex);

		return (
			<Flex justifyContent="center">
				<Box width={[1, 1 / 2]} bg="background2">
					<Text color="text2">SubmissionView.jsx</Text>
					<ReactResizeDetector handleWidth>
						<SentenceSlide
							sentence={sentences[currentSentenceIndex]}
						/>
					</ReactResizeDetector>

					<Text color="text2">Hello</Text>
				</Box>
			</Flex>
		);
	}
}

const mapStateToProps = ({ submissionViewReducer }) => ({
	details: submissionViewReducer.details,
	sentences: submissionViewReducer.sentences,
	currentSentenceIndex: submissionViewReducer.currentSentenceIndex
});
const mapDispatchToProps = dispatch => ({});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SubmissionView);

class SentenceSlide extends PureComponent {
	constructor(props) {
		super(props);
	}
	render() {
		const { sentence, width } = this.props;
		const height = isNaN(width) ? 0 : (width * 9) / 16;
		const imgSrc = sentence
			? `${apiUrl}/ui/sentences/${sentence.id}/slide`
			: "";

		return (
			<Box style={{ position: "relative", height }}>
				<Image src={imgSrc} style={{ position: "absolute" }} />
				<Box
					bg="pink"
					style={{
						position: "absolute",
						width: "100%",
						height: "100%",
						opacity: "0.5"
					}}
				>
					<Text>Absolute position test</Text>
				</Box>
			</Box>
		);
	}
}
