.PHONY: handin answers/group.txt answers/report.pdf

course:=$(shell cat .course)
hw:=$(notdir $(realpath $(dir $(firstword $(MAKEFILE_LIST)))))

handin: answers/group.txt answers/report.pdf
	@rutool handin -c $(course) -p $(hw) answers src dist build.xml
	@rutool check -c $(course) -p $(hw)

answers/group.txt:
	@ cd questions; ./check_group_file.sh

answers/report.pdf:
	@ if [ ! -r $@ ] ; then \
		echo "File '$@' not found! Please make sure to copy the report in place and re-run 'make handin'" ; \
	fi
