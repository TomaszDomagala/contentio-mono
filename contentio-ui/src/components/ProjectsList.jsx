import React, { Component } from "react";
import { connect } from "react-redux";
import axios from "axios";
import { Link } from "react-router-dom";
import { Box, Text, Card } from "rebass";
import { getProjectsPage } from "../store/projects/actions";

class ProjectListItem extends Component {
	state = {
		title: ""
	};

	componentDidMount() {
		const id = this.props.project.id;
		axios
			.get(`http://192.168.1.11:8080/projects/${id}/title`)
			.then(({ data }) => {
				this.setState({ title: data });
			});
	}

	render() {
		const { project } = this.props;
		return (
			<Link
				to={`/projects/${project.id}`}
				style={{ textDecoration: "none" }}
			>
				<Card
					my={2}
					p={3}
					borderColor="line"
					borderStyle="solid"
					border={1}
					borderRadius={8}
				>
					<Text color="text1">
						Id: {project.id} {this.state.title}
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
				{this.props.projectsPage.content.map(project => (
					<ProjectListItem key={project.id} project={project} />
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
