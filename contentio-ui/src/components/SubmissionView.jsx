import React, { Component } from "react";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";
import { connect } from "react-redux";
import { apiUrl } from "../utils/urls";
import { formatSec } from "../utils/formatting";

class SubmissionView extends Component {
	render() {
		return (
			<Flex justifyContent="center">
				<Box width={[1, 1 / 2]} bg="background2">
					<Text color="text2">SubmissionView.jsx</Text>
					<Text color="text1">
						{JSON.stringify(this.props.submissionDetails)}
					</Text>
				</Box>
			</Flex>
		);
	}
}

const mapStateToProps = ({ projectViewReducer }) => ({
	submissionDetails: projectViewReducer.submissionDetails
});
const mapDispatchToProps = dispatch => ({});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SubmissionView);
