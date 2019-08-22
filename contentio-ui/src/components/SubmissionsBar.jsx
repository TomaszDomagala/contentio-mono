import React, { Component } from "react";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";
import { connect } from "react-redux";
import { apiUrl } from "../utils/urls";
import { formatSec } from "../utils/formatting";

class SubmissionsBar extends Component {
	render() {
		const { submissions } = this.props;
		return (
			<Box bg="background1" py={3}>
				<Flex className="horizontal-scroll no-scroll-bar">
					{submissions.map((submission, index) => (
						<BarItem
							submission={submission}
							onClick={console.log}
						/>
					))}
				</Flex>
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
)(SubmissionsBar);

const BarItem = props => {
	const { submission, onClick } = props;
	const imgSrc = `${apiUrl}/ui/submissions/${submission.id}/slide`;
	console.log(submission);
	return (
		<Card
			{...props}
			mx={2}
			style={{ minWidth: "256px" }}
			onClick={() => onClick(submission.id)}
		>
			<Image src={imgSrc} />
			<Text color="text2">{formatSec(submission.audioDuration)}</Text>
			<Text color="text2">{submission.id}</Text>
		</Card>
	);
};
