import React, { Component } from "react";
import { connect } from "react-redux";
import { Box, Flex, Card, Text, Heading } from "rebass";
import SubmissionsBar from "./SubmissionsBar";
import SubmissionView from "./SubmissionView";
import { fetchProjectDetails } from "../store/projectview/actions";
import ProjectDetails from "./ProjectDetails";

class ProjectView extends Component {
	componentDidMount() {
		const { projectId } = this.props.match.params;
		this.props.fetchDetails(projectId);
	}

	render() {
		return (
			<Box bg="background" pt={2} pb={5} style={{ minHeight: "100vh" }} >
				<ProjectDetails />
				<SubmissionsBar />
				<SubmissionView />
			</Box>
		);
	}
}

const mapStateToProps = state => ({});
const mapDispatchToProps = dispatch => ({
	fetchDetails: id => dispatch(fetchProjectDetails(id))
});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(ProjectView);
