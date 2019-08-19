import React, { Component } from "react";
import { connect } from "react-redux";
import { Box, Flex, Card, Text, Heading } from "rebass";
import { clearSubmission } from "../store/submissiondetails/actions";

class SubmissionDetails extends Component {
	render() {
		const { id, author, score, originalText, editedText } = this.props;
		return <Box>
            <Text>{id}</Text>
            <Text>{author}</Text>
            <Text>{score}</Text>
            <Text>{originalText}</Text>
            <Text>{editedText}</Text>
        </Box>;
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
