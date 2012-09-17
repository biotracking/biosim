all:
	make -C src/
	make -C mason/
commit:
	make -C src/
	make -C mason/
	svn commit
