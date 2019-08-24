import React, { Component, PureComponent } from "react";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";
import ReactResizeDetector from "react-resize-detector";
import { connect } from "react-redux";
import { setCurrentSentence } from "../store/submissionview/actions";
import { apiUrl } from "../utils/urls";
import { formatSec } from "../utils/formatting";
import { IconContext } from "react-icons";
import {
	MdKeyboardArrowLeft,
	MdKeyboardArrowRight,
	MdErrorOutline
} from "react-icons/md";

class SubmissionView extends PureComponent {
	render() {
		const {
			details,
			sentences,
			sentenceView,
			setCurrentSentence
		} = this.props;

		// console.log(details);
		// console.log(sentences);
		// console.log(currentSentenceIndex);

		return (
			<Flex justifyContent="center">
				<Box width={[1, 1 / 2]} bg="background2">
					<Text color="text2">SubmissionView.jsx</Text>
					<ReactResizeDetector handleWidth>
						<SentenceSlide
							sentence={sentences[sentenceView.currentIndex]}
							first={sentenceView.first}
							last={sentenceView.last}
							setCurrentSentence={setCurrentSentence}
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
	sentenceView: submissionViewReducer.sentenceView
});
const mapDispatchToProps = dispatch => ({
	setCurrentSentence: index => dispatch(setCurrentSentence(index))
});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SubmissionView);

class SentenceSlide extends PureComponent {
	constructor(props) {
		super(props);
		this.incrementSentence = this.incrementSentence.bind(this);
		this.decrementSentence = this.decrementSentence.bind(this);
	}

	incrementSentence() {
		const { sentence, last, setCurrentSentence } = this.props;
		if (!last) {
			setCurrentSentence(sentence.index + 1);
		}
	}
	decrementSentence() {
		const { sentence, first, setCurrentSentence } = this.props;
		if (!first) {
			setCurrentSentence(sentence.index - 1);
		}
	}
	render() {
		const { sentence, first, last, width } = this.props;
		const height = isNaN(width) ? 0 : (width * 9) / 16;
		const imgSrc = sentence
			? `${apiUrl}/ui/sentences/${sentence.id}/slide`
			: "";

		return (
			<Box style={{ position: "relative", height }}>
				<Image src={imgSrc} style={{ position: "absolute" }} />
				<Flex
					justifyContent="space-between"
					style={{
						position: "absolute",
						width: "100%",
						height: "100%"
					}}
				>
					<SlideButton
						direction="left"
						disabled={first}
						onClick={this.decrementSentence}
					/>
					<SlideButton
						direction="right"
						disabled={last}
						onClick={this.incrementSentence}
					/>
				</Flex>
			</Box>
		);
	}
}

const SlideButton = ({ direction, disabled, onClick }) => {
	let icon;
	switch (direction) {
		case "right": {
			icon = <MdKeyboardArrowRight />;
			break;
		}
		case "left": {
			icon = <MdKeyboardArrowLeft />;
			break;
		}
		default: {
			icon = <MdErrorOutline />;
			break;
		}
	}
	const visibility = disabled ? "hidden" : "visible";
	return (
		<Flex
			flexDirection="column"
			justifyContent="center"
			width={[1 / 7, 1 / 16]}
			bg="rgba(255,255,255,0.1)"
			style={{ height: "100%", cursor: "pointer", visibility }}
			onClick={onClick}
		>
			<Text alignSelf="center">
				<IconContext.Provider value={{ color: "white", size: "2rem" }}>
					{icon}
				</IconContext.Provider>
			</Text>
		</Flex>
	);
};