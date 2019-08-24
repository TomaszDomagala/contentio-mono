import React, { Component } from "react";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";
import { connect } from "react-redux";
import { fetchSubmissionDetails } from "../store/submissionview/actions";
import { apiUrl } from "../utils/urls";
import { formatSec } from "../utils/formatting";
import Thumbnail from "../containers/Thumbnail";

class SubmissionsBar extends Component {
	render() {
		const { submissions } = this.props;
		return (
			<Box bg="background1" py={3}>
				<Flex className="horizontal-scroll no-scroll-bar">
					{submissions.map(submission => (
						<BarItem
							key={submission.id}
							submission={submission}
							onClick={this.props.changeSubmission}
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
const mapDispatchToProps = dispatch => ({
	changeSubmission: submissionId =>
		dispatch(fetchSubmissionDetails(submissionId))
});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SubmissionsBar);

const BarItem = props => {
	const { submission, onClick } = props;
	const imgSrc = `${apiUrl}/ui/submissions/${submission.id}/slide`;
	const width = 256;
	const height = 256 / (16 / 9);
	return (
		<Box
			{...props}
			mx={2}
			style={{
				cursor: "pointer",
				minWidth: width,
				maxWidth: width,
				height,
				backgroundImage: `url(${imgSrc})`,
				backgroundSize: "cover"
			}}
			onClick={() => onClick(submission.id)}
		>
			<Flex flexDirection="column-reverse" style={{ height: "100%" }}>
				<Flex flexDirection="row-reverse">
					<Text m={1} color="text2">
						{formatSec(submission.audioDuration)}
					</Text>
				</Flex>
			</Flex>
		</Box>
	);
};
