import React, { Component } from "react";
import { Box, Flex, Card, Text, Heading } from "rebass";
import ProjectDetails from "./ProjectDetails";
import SubmissionDetails from "./SubmissionDetails";

class ProjectEditor extends Component {

	

	render() {
		const { projectId } = this.props.match.params;
		return (
			<Box bg="background" style={{ minHeight: "100vh" }}>
				<Flex >
					<Box  width={[1, 2 / 5]}>
						<ProjectDetails projectId={projectId} />
					</Box>
					<Box width={[0, 2 / 5]}>
						<SubmissionDetails />
					</Box>
				</Flex>
			</Box>
		);
	}
}

export default ProjectEditor;
