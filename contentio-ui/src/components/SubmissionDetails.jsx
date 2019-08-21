import React, { Component } from "react";
import { connect } from "react-redux";
import { Box, Flex, Card, Text, Heading } from "rebass";
import { clearSubmission } from "../store/submissiondetails/actions";

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
			<Box>
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
							u/{author} {id} {score}
						</Text>
						
					</Box>

					<Text fontSize={3}>{editedText}</Text>
				</Card>
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
