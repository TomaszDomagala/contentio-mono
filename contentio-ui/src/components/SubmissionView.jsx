import React, { Component } from "react";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";
import { connect } from "react-redux";
import { apiUrl } from "../utils/urls";
import { formatSec } from "../utils/formatting";

class SubmissionView extends Component {
	render() {
		return (
			<Box>
				<Text color="text2">SubmissionView.jsx</Text>
			</Box>
		);
	}
}

const mapStateToProps = ({ projectViewReducer }) => ({
	submissions: projectViewReducer.submissions
});
const mapDispatchToProps = dispatch => ({});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SubmissionView);
