import React, { Component } from "react";
import { connect } from "react-redux";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";
import { clearSubmission } from "../store/submissiondetails/actions";
import { Link } from "react-router-dom";
import { apiUrl } from "../utils/urls";

class SubmissionDetails extends Component {
	render() {
		const {
			id,
			author,
			score,
			originalText,
			editedText
		} = this.props.submission;
		if (!this.props.selected) return null;

		return (
			<Box
				p={3}
				bg="background2"
				className="no-scroll-bar"
				style={{
					height: "100vh",
					overflow: "hidden",
					overflowY: "scroll"
				}}
			>
				<Card
					p={3}
					borderColor="line"
					borderStyle="solid"
					border={1}
					borderRadius={8}
				>
					<Box mb={3}>
						<Heading>{id} details</Heading>
						<Text>
							user: {author} score: {score}
						</Text>
					</Box>

					<Text fontSize={3}>{editedText}</Text>
				</Card>
				<SentencesList sentences={this.props.sentences} />
			</Box>
		);
	}
}

const mapStateToProps = ({ submissionDetailsReducer: details }) => ({
	...details
});
const mapDispatchToProps = dispatch => ({
	clearSubmission: () => dispatch(clearSubmission())
});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SubmissionDetails);

const SentencesList = ({ sentences }) => {
	return (
		<Box>
			{sentences.map(sentence => {
				return (
					<Card key={sentence.id} mt={3}>
						<Flex>
							{sentence.slideGenerated && (
								<SlideThumbnail sentenceId={sentence.id} />
							)}
							<Box>
								<IsGenerated
									isGenerated={sentence.slideGenerated}
								>
									Slide generated:{" "}
								</IsGenerated>
								<IsGenerated
									isGenerated={sentence.audioGenerated}
								>
									Audio generated:{" "}
								</IsGenerated>

								<Text fontSize={1}>
									Id:{sentence.id} {sentence.text}
								</Text>
							</Box>
						</Flex>
					</Card>
				);
			})}
		</Box>
	);
};

const SlideThumbnail = ({ sentenceId }) => (
	<Box mx={2}>
		<a
			target="_blank"
			rel="noopener noreferrer"
			href={`${apiUrl}/ui/sentences/${sentenceId}/slide`}
		>
			<Image
				style={{ width: "190px" }}
				src={`${apiUrl}/ui/sentences/${sentenceId}/slide`}
			/>
		</a>
	</Box>
);

const IsGenerated = ({ isGenerated, children }) => (
	<Text fontSize={1}>
		{children}{" "}
		<span style={{ color: isGenerated ? "green" : "red" }}>
			{isGenerated.toString()}
		</span>
	</Text>
);
