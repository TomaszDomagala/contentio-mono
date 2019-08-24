import React, { Component } from "react";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import { Box, Text, Card } from "rebass";
import { getProjectsPage } from "../store/projects/actions";

class ProjectListItem extends Component {
	render() {
		const { projectId, title } = this.props;
		return (
			<Link
				to={`/projects/${projectId}`}
				style={{ textDecoration: "none" }}
			>
				<Card
					my={2}
					p={3}
					borderColor="divider"
					borderStyle="solid"
					border={1}
					borderRadius={8}
				>
					<Text color="text1">
						Id: {projectId} {title}
					</Text>
				</Card>
			</Link>
		);
	}
}

class ProjectsList extends Component {
	componentDidMount() {
		this.props.requestPage(0);
	}

	render() {
		return (
			<Box mt={3}>
				{this.props.projectsPage.content.map(({ projectId, title }) => (
					<ProjectListItem
						key={projectId}
						projectId={projectId}
						title={title}
					/>
				))}
			</Box>
		);
	}
}

const mapStateToProps = state => ({
	projectsPage: state.projectsReducer.projectsPage
});

const mapDispatchToProps = dispatch => ({
	requestPage: page => dispatch(getProjectsPage(page))
});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(ProjectsList);
